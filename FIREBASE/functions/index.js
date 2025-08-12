/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

// Firestore trigger for new orders
const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

admin.initializeApp();
const db = admin.firestore();

exports.createEmailDocument = onDocumentCreated("order/{orderId}", async (event) => {
  const snapshot = event.data;
  const order = snapshot ? snapshot.data() : null;
  if (!order) {
    console.log("No order data.");
    return null;
  }

  try {
    // 1) Customer
    const customerDoc = await db.collection("customer").doc(order.customerId).get();
    const customer = customerDoc.exists ? customerDoc.data() : null;

    // 2) Products (title, price, thumbnail, badges)
    const products = await Promise.all(
      (order.items || []).map(async (item) => {
        const doc = await db.collection("product").doc(item.productId).get();
        if (!doc.exists) return null;
        const d = doc.data() || {};
        return {
          title: d.title,
          price: d.price,
          thumbnail: d.thumbnail,      // public Storage URL
          isNew: !!d.isNew,
          isPopular: !!d.isPopular,
          isDiscounted: !!d.isDiscounted,
        };
      })
    );

    const html = buildOrderEmail({ order, customer, products });
    const text = buildTextEmail({ order, customer, products });

    await db.collection("mail").add({
      to: ["federico.debenedictis.nl@gmail.com"],
      message: {
        subject: `NutriSportDemo • Order ${order.id} confirmed`,
        html,
        text,
      },
    });

    console.log("Mail document created.");
  } catch (e) {
    console.error("Failed creating email:", e);
  }
  return null;
});

/* ---------------- Email template (NutriSportDemo) ---------------- */

const BRAND = {
  name: "NutriSportDemo",
  bg: "#FAFAFA",           // GrayLighter
  cardBg: "#FFFFFF",       // White
  headerBg: "#000000",     // Black
  headerAccent: "#FFFFFF", // <-- Title in WHITE now
  border: "#EBEBEB",       // GrayDarker
  text: "#000000",         // TextPrimary
  subtext: "#666666",
  buttonBg: "#FEFF00",     // Yellowish (lighter)
  buttonText: "#000000",   // Black text
  buttonBorder: "#E6E6E6",
  badgeNew: "#19D109",
  badgePopular: "#38B3FF",
  badgeDiscount: "#FF5E60",
};

const SHOW_BADGES = false; // badges off by default

function esc(s = "") {
  return String(s).replace(/[&<>"']/g, (c) => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#039;",
  }[c]));
}

function formatCurrency(n, currency = "EUR", locale = "nl-NL") {
  const num = Number(n || 0);
  try {
    return new Intl.NumberFormat(locale, { style: "currency", currency }).format(num);
  } catch {
    return `€${num.toFixed(2)}`;
  }
}

function buildItemRows(items = [], products = [], currency = "EUR") {
  return (items || []).map((item, i) => {
    const p = products[i] || {};
    const qty = item.quantity || 1;
    const unit = Number(typeof p.price === "number" ? p.price : (item.price || 0));
    const total = unit * qty;

    const badges = SHOW_BADGES
      ? [
          p.isNew ? `<span style="display:inline-block;padding:2px 8px;border-radius:9999px;background:${BRAND.badgeNew};color:#000;font:600 11px Arial;margin-left:6px;">NEW</span>` : "",
          p.isPopular ? `<span style="display:inline-block;padding:2px 8px;border-radius:9999px;background:${BRAND.badgePopular};color:#000;font:600 11px Arial;margin-left:6px;">POPULAR</span>` : "",
          p.isDiscounted ? `<span style="display:inline-block;padding:2px 8px;border-radius:9999px;background:${BRAND.badgeDiscount};color:#000;font:600 11px Arial;margin-left:6px;">DISCOUNT</span>` : ""
        ].join("")
      : "";

    const imgCell = p.thumbnail
      ? `<td width="72" style="padding-right:12px;">
           <img src="${esc(p.thumbnail)}" width="72" height="72"
                style="display:block;border-radius:10px;border:1px solid ${BRAND.border};object-fit:cover;" alt="">
         </td>`
      : `<td width="72" style="padding-right:12px;">
           <div style="width:72px;height:72px;border:1px dashed ${BRAND.border};border-radius:10px;"></div>
         </td>`;

    return `
<tr>
  <td style="padding:14px 0;border-bottom:1px solid ${BRAND.border};">
    <table role="presentation" width="100%" cellpadding="0" cellspacing="0">
      <tr>
        ${imgCell}
        <td style="font:14px/1.4 Arial,Helvetica,sans-serif;color:${BRAND.text};">
          <div style="font-weight:700;">${esc(p.title || "Unknown product")}${badges}</div>
          ${item.flavor ? `<div style="color:${BRAND.subtext};margin-top:2px;">Flavor: ${esc(item.flavor)}</div>` : ""}
          <div style="color:${BRAND.subtext};margin-top:2px;">Qty: ${qty}</div>
        </td>
        <td align="right" style="font:14px/1.4 Arial,Helvetica,sans-serif;white-space:nowrap;">
          <div>${formatCurrency(unit, currency)}</div>
          <div style="color:${BRAND.subtext};">${formatCurrency(total, currency)}</div>
        </td>
      </tr>
    </table>
  </td>
</tr>`;
  }).join("");
}

function buildOrderEmail({ order, customer, products }) {
  const currency = order.currency || "EUR";
  const created = new Date(order.createdAt || Date.now()).toLocaleString("nl-NL");
  const payment = order.token ? `PayPal (${esc(order.token)})` : "Pay on delivery";

  const subtotal = Number(order.subtotal ?? order.totalAmount ?? 0);
  const shipping = Number(order.shippingFee ?? 0);
  const tax = Number(order.tax ?? 0);
  const total = Number(order.totalAmount ?? (subtotal + shipping + tax));

  const itemsTable = buildItemRows(order.items || [], products || [], currency);

  return `<!doctype html>
<html>
  <body style="margin:0;padding:0;background:${BRAND.bg};">
    <!-- Preheader -->
    <div style="display:none;opacity:0;visibility:hidden;mso-hide:all;height:0;overflow:hidden;">
      Thanks! Your NutriSportDemo order ${esc(order.id || "")} is confirmed.
    </div>

    <!-- Wrapper table ensures safe side padding on Gmail Android -->
    <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background:${BRAND.bg};">
      <tr>
        <td align="center" style="padding:0 16px;">

          <table role="presentation" width="100%" cellpadding="0" cellspacing="0"
                 style="max-width:600px;background:${BRAND.cardBg};border-radius:14px;overflow:hidden;border:1px solid ${BRAND.border};">
            <tr>
              <td style="background:${BRAND.headerBg};padding:20px 24px;">
                <table role="presentation" width="100%" cellpadding="0" cellspacing="0">
                  <tr>
                    <td>
                      <div style="font:800 20px/1 Arial,Helvetica,sans-serif;color:${BRAND.headerAccent};letter-spacing:.2px;">
                        ${esc(BRAND.name)}
                      </div>
                      <div style="font:13px Arial,Helvetica,sans-serif;color:#cbd5e1;margin-top:6px;">
                        Order ${esc(order.id || "")} • ${esc(created)}
                      </div>
                    </td>
                    <td align="right">
                      <div style="font:700 16px Arial,Helvetica,sans-serif;color:#ffffff;">
                        ${formatCurrency(total, currency)}
                      </div>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>

            <tr>
              <td style="padding:22px 24px 6px;">
                <div style="font:600 18px Arial,Helvetica,sans-serif;margin:0 0 8px;color:${BRAND.text};">
                  Order summary
                </div>

                <table role="presentation" width="100%" cellpadding="0" cellspacing="0">
                  ${itemsTable}
                </table>

                <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="margin-top:12px">
                  <tr>
                    <td style="color:${BRAND.subtext};font:14px Arial,Helvetica,sans-serif;">Subtotal</td>
                    <td align="right" style="font:14px Arial,Helvetica,sans-serif;">
                      ${formatCurrency(subtotal, currency)}
                    </td>
                  </tr>
                  ${shipping ? `
                  <tr>
                    <td style="color:${BRAND.subtext};font:14px Arial,Helvetica,sans-serif;">Shipping</td>
                    <td align="right" style="font:14px Arial,Helvetica,sans-serif;">
                      ${formatCurrency(shipping, currency)}
                    </td>
                  </tr>` : ""}
                  ${tax ? `
                  <tr>
                    <td style="color:${BRAND.subtext};font:14px Arial,Helvetica,sans-serif;">Tax</td>
                    <td align="right" style="font:14px Arial,Helvetica,sans-serif;">
                      ${formatCurrency(tax, currency)}
                    </td>
                  </tr>` : ""}
                  <tr>
                    <td style="padding-top:8px;border-top:1px solid ${BRAND.border};font:700 15px Arial,Helvetica,sans-serif;color:${BRAND.text};">
                      Total
                    </td>
                    <td align="right" style="padding-top:8px;border-top:1px solid ${BRAND.border};font:700 15px Arial,Helvetica,sans-serif;color:${BRAND.text};">
                      ${formatCurrency(total, currency)}
                    </td>
                  </tr>
                </table>

                <div style="margin:16px 0 20px;font:14px Arial,Helvetica,sans-serif;color:${BRAND.text};">
                  <strong>Payment:</strong> ${esc(payment)}
                </div>

                <!-- Material-ish, lighter button — NOT clickable -->
                <span role="button"
                      style="display:inline-block;padding:14px 20px;border-radius:12px;
                             background:${BRAND.buttonBg};color:${BRAND.buttonText};
                             font:700 14px Arial,Helvetica,sans-serif;border:1px solid ${BRAND.buttonBorder};
                             cursor:default;">
                  View order
                </span>

                <div style="margin-top:24px;font:14px Arial,Helvetica,sans-serif;color:${BRAND.text};">
                  <div style="font-weight:700;margin-bottom:6px;">Customer</div>
                  <div>${esc(customer?.firstName || "N/A")} ${esc(customer?.lastName || "")}</div>
                  <div>${esc(customer?.email || "")}</div>
                  <div>${esc(customer?.address || "")}</div>
                  <div>${esc(customer?.postalCode || "")} ${esc(customer?.city || "")}</div>
                  ${customer?.phoneNumber ? `<div>+${esc(customer.phoneNumber.dialCode)} ${esc(customer.phoneNumber.number)}</div>` : ""}
                </div>
              </td>
            </tr>

            <tr>
              <td style="padding:14px 24px;background:${BRAND.bg};color:#7c7c7c;font:12px Arial,Helvetica,sans-serif;border-top:1px solid ${BRAND.border};">
                Questions? Reply to this email and we’ll help.
              </td>
            </tr>
          </table>

        </td>
      </tr>
    </table>

    <div style="text-align:center;margin-top:10px;color:#9aa0a6;font:12px Arial,Helvetica,sans-serif;">
      © ${new Date().getFullYear()} ${esc(BRAND.name)}
    </div>
  </body>
</html>`;
}

function buildTextEmail({ order, customer, products }) {
  const currency = order.currency || "EUR";
  const parts = [];
  parts.push(`Order ${order.id}`);
  parts.push(`Total: ${formatCurrency(order.totalAmount, currency)}`);
  parts.push(`Payment: ${order.token ? "PayPal" : "Pay on delivery"}`);
  parts.push("");
  parts.push("Items:");
  (order.items || []).forEach((item, i) => {
    const p = products[i] || {};
    parts.push(`- ${p.title || "Unknown product"}${item.flavor ? ` (${item.flavor})` : ""} x${item.quantity || 1}`);
  });
  parts.push("");
  parts.push(`Customer: ${customer?.firstName || ""} ${customer?.lastName || ""}`);
  parts.push(customer?.email || "");
  parts.push(`${customer?.address || ""}, ${customer?.postalCode || ""} ${customer?.city || ""}`);
  return parts.join("\n");
}

