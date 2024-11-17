Coupon Management API
Overview
The Coupon Management API is a Spring Boot-based backend system designed for an e-commerce platform. It enables the management and application of discount coupons to shopping carts. The API supports:

Cart-wise Coupons: Discounts based on the total cart value.
Product-wise Coupons: Discounts applied to specific products.
BxGy Coupons: "Buy X, Get Y" deals for specific product groups.
Implemented Cases
1. Cart-wise Coupons
Example: 10% off on carts over ₹100.
Logic:
If the cart's total value exceeds a specified threshold, the discount is applied as a percentage of the total value.

Payload Example:
json
{
  "type": "CART_WISE",
  "details": { "threshold": 100, "discount": 10 },
  "active": true
}
2. Product-wise Coupons
Example: 20% off on Product A.
Logic:
Discounts are applied to specific products in the cart.

Payload Example:
json

{
  "type": "PRODUCT_WISE",
  "details": { "product_id": 1, "discount": 20 },
  "active": true
}
3. BxGy Coupons
Example: Buy 2 items from Product X, Y, Z and get 1 item from Product A, B, C for free.
Logic:
Verifies if the cart contains sufficient quantities of the "buy" products to determine how many "get" products can be offered for free.

Payload Example:
json

{
  "type": "BxGy",
  "details": {
    "buy_products": [
      { "product_id": 1, "quantity": 2 },
      { "product_id": 2, "quantity": 3 }
    ],
    "get_products": [
      { "product_id": 3, "quantity": 1 }
    ],
    "repetition_limit": 2
  },
  "active": true
}
Unimplemented Cases
Coupon Stacking

Description: Combining multiple coupons for a single cart.
Reason: Requires prioritization logic for overlapping discounts.
Dynamic Coupon Expiry

Description: Support for expiry dates and times.
Reason: Time constraints during development.
Multi-Currency Support

Description: Coupons for carts with multiple currencies.
Reason: Requires integration with currency conversion services.
Limitations
Static Cart Structure:
The current implementation assumes a fixed structure for the cart (Cart and CartItem entities).

No Validation for Coupon Expiry:
Coupons remain applicable regardless of their validity period.

Limited Scalability for Complex Rules:
The system does not support advanced coupon rules such as category-based discounts or region-based restrictions.

No Multi-Coupon Support:
Only one coupon can be applied per cart at a time.

Assumptions
Cart Items:

Each item in the cart has a productId, price, and quantity.
All items belong to the same currency and region.
Coupon Details:

The details field in the coupon is a valid JSON object.
All thresholds, discounts, and product IDs are properly defined in the JSON.
BxGy Coupons:

The "buy" and "get" products are uniquely identified by their productId.
Repetition limits are strictly adhered to.
Endpoints Documentation
Endpoint	                     Method	Description
/api/coupons	                 GET	Fetch all available coupons.
/api/coupons/{couponId}	         GET	Fetch details of a specific coupon.
/api/coupons	                 POST	Create a new coupon.
/api/coupons/{couponId}	         PUT	Update an existing coupon.
/api/coupons/{couponId}	         DELETE	Delete a coupon.
/api/coupons/applicable	         POST	Get all applicable coupons for a given cart.
/api/coupons/apply/{couponId}	 POST	Apply a specific coupon to the cart.

Request Examples
1. Create a Coupon
Endpoint: POST /api/coupons
Payload:
json
{
  "type": "CART_WISE",
  "details": { "threshold": 200, "discount": 15 },
  "active": true
}

2. Get Applicable Coupons
Endpoint: POST /api/coupons/applicable
Payload:
json
{
  "items": [
    { "productId": 1, "quantity": 2, "price": 50 },
    { "productId": 2, "quantity": 1, "price": 30 }
  ]
}

3. Apply a Coupon
Endpoint: POST /api/coupons/apply/{couponId}
Payload:
json
{
  "items": [
    { "productId": 1, "quantity": 6, "price": 50 },
    { "productId": 2, "quantity": 3, "price": 30 }
  ]
}
Future Enhancements
Coupon Expiry:
Add functionality to validate and enforce coupon expiry dates.

Coupon Stacking:
Allow users to combine multiple coupons in a single cart.

Region and Category-Specific Discounts:
Introduce conditions for applying coupons based on product categories or user regions.

Audit Logs:
Track coupon usage for analytical purposes.