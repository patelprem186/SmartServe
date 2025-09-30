const stripe = require('stripe')(process.env.STRIPE_SECRET_KEY);
const paypal = require('paypal-rest-sdk');

class PaymentService {
  constructor() {
    this.initializePayPal();
  }

  initializePayPal() {
    paypal.configure({
      mode: process.env.PAYPAL_MODE || 'sandbox',
      client_id: process.env.PAYPAL_CLIENT_ID,
      client_secret: process.env.PAYPAL_CLIENT_SECRET
    });
  }

  async processPayment({ bookingId, amount, paymentMethod, cardDetails, billingAddress, customer, provider, service }) {
    try {
      let result;

      switch (paymentMethod) {
        case 'stripe':
          result = await this.processStripePayment({
            amount,
            cardDetails,
            billingAddress,
            customer,
            service
          });
          break;
        case 'paypal':
          result = await this.processPayPalPayment({
            amount,
            customer,
            service
          });
          break;
        default:
          throw new Error('Unsupported payment method');
      }

      return result;
    } catch (error) {
      console.error('Process payment error:', error);
      return { success: false, error: error.message };
    }
  }

  async processStripePayment({ amount, cardDetails, billingAddress, customer, service }) {
    try {
      const paymentIntent = await stripe.paymentIntents.create({
        amount: Math.round(amount * 100), // Convert to cents
        currency: 'usd',
        payment_method_data: {
          type: 'card',
          card: {
            number: cardDetails.number,
            exp_month: cardDetails.expMonth,
            exp_year: cardDetails.expYear,
            cvc: cardDetails.cvc
          },
          billing_details: {
            name: `${customer.firstName} ${customer.lastName}`,
            email: customer.email,
            address: billingAddress
          }
        },
        confirm: true,
        description: `Payment for ${service.name} service`,
        metadata: {
          customerId: customer._id,
          serviceId: service._id
        }
      });

      return {
        success: true,
        paymentId: paymentIntent.id,
        amount: amount,
        status: paymentIntent.status
      };
    } catch (error) {
      console.error('Stripe payment error:', error);
      return { success: false, error: error.message };
    }
  }

  async processPayPalPayment({ amount, customer, service }) {
    try {
      const payment = {
        intent: 'sale',
        payer: {
          payment_method: 'paypal'
        },
        transactions: [{
          amount: {
            currency: 'USD',
            total: amount.toString()
          },
          description: `Payment for ${service.name} service`,
          item_list: {
            items: [{
              name: service.name,
              sku: service._id,
              price: amount.toString(),
              currency: 'USD',
              quantity: 1
            }]
          }
        }],
        redirect_urls: {
          return_url: `${process.env.CLIENT_URL}/payment/success`,
          cancel_url: `${process.env.CLIENT_URL}/payment/cancel`
        }
      };

      const result = await new Promise((resolve, reject) => {
        paypal.payment.create(payment, (error, payment) => {
          if (error) {
            reject(error);
          } else {
            resolve(payment);
          }
        });
      });

      return {
        success: true,
        paymentId: result.id,
        amount: amount,
        status: 'pending',
        approvalUrl: result.links.find(link => link.rel === 'approval_url')?.href
      };
    } catch (error) {
      console.error('PayPal payment error:', error);
      return { success: false, error: error.message };
    }
  }

  async refundPayment({ paymentId, amount, reason }) {
    try {
      const refund = await stripe.refunds.create({
        payment_intent: paymentId,
        amount: Math.round(amount * 100), // Convert to cents
        reason: 'requested_by_customer',
        metadata: {
          reason: reason || 'Customer requested refund'
        }
      });

      return {
        success: true,
        refundId: refund.id,
        amount: amount,
        status: refund.status
      };
    } catch (error) {
      console.error('Refund payment error:', error);
      return { success: false, error: error.message };
    }
  }

  async getPaymentStatus(paymentId) {
    try {
      const paymentIntent = await stripe.paymentIntents.retrieve(paymentId);
      
      return {
        paymentId: paymentIntent.id,
        status: paymentIntent.status,
        amount: paymentIntent.amount / 100, // Convert from cents
        currency: paymentIntent.currency,
        created: paymentIntent.created,
        charges: paymentIntent.charges.data
      };
    } catch (error) {
      console.error('Get payment status error:', error);
      throw error;
    }
  }
}

module.exports = new PaymentService();
