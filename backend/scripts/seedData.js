const mongoose = require('mongoose');
const dotenv = require('dotenv');
const bcrypt = require('bcryptjs');

// Load environment variables
dotenv.config({ path: './config.env' });

// Import models
const User = require('../models/User');
const ServiceCategory = require('../models/ServiceCategory');
const Service = require('../models/Service');
const Booking = require('../models/Booking');

// Connect to MongoDB
mongoose.connect(process.env.MONGODB_URI || 'mongodb://localhost:27017/easybook', {
  useNewUrlParser: true,
  useUnifiedTopology: true,
});

const seedData = async () => {
  try {
    console.log('🌱 Starting data seeding...');

    // Clear existing data
    await User.deleteMany({});
    await ServiceCategory.deleteMany({});
    await Service.deleteMany({});
    await Booking.deleteMany({});
    console.log('🗑️  Cleared existing data');

    // Create service categories
    const categories = [
      {
        name: 'Home Cleaning',
        description: 'Professional home cleaning services',
        icon: 'ic_cleaning',
        color: '#4CAF50',
        sortOrder: 1
      },
      {
        name: 'Plumbing',
        description: 'Plumbing repair and installation services',
        icon: 'ic_plumbing',
        color: '#2196F3',
        sortOrder: 2
      },
      {
        name: 'Electrical',
        description: 'Electrical repair and installation services',
        icon: 'ic_electrical',
        color: '#FF9800',
        sortOrder: 3
      },
      {
        name: 'HVAC',
        description: 'Heating, ventilation, and air conditioning services',
        icon: 'ic_hvac',
        color: '#9C27B0',
        sortOrder: 4
      },
      {
        name: 'Landscaping',
        description: 'Garden and lawn maintenance services',
        icon: 'ic_landscaping',
        color: '#8BC34A',
        sortOrder: 5
      },
      {
        name: 'Painting',
        description: 'Interior and exterior painting services',
        icon: 'ic_painting',
        color: '#E91E63',
        sortOrder: 6
      },
      {
        name: 'Carpentry',
        description: 'Woodworking and furniture repair services',
        icon: 'ic_carpentry',
        color: '#795548',
        sortOrder: 7
      },
      {
        name: 'Appliance Repair',
        description: 'Home appliance repair and maintenance',
        icon: 'ic_appliance',
        color: '#607D8B',
        sortOrder: 8
      }
    ];

    const createdCategories = await ServiceCategory.insertMany(categories);
    console.log('✅ Created service categories');

    // Create admin user
    const adminUser = new User({
      firstName: 'Admin',
      lastName: 'User',
      email: 'admin@easybook.com',
      phone: '+1234567890',
      password: 'admin123',
      role: 'admin',
      isVerified: true
    });
    await adminUser.save();
    console.log('✅ Created admin user');

    // Create customer users
    const customers = [
      {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@email.com',
        phone: '+1234567891',
        password: 'password123',
        role: 'customer',
        customerInfo: {
          address: {
            street: '123 Main St',
            city: 'New York',
            state: 'NY',
            zipCode: '10001',
            coordinates: { latitude: 40.7128, longitude: -74.0060 }
          }
        }
      },
      {
        firstName: 'Jane',
        lastName: 'Smith',
        email: 'jane.smith@email.com',
        phone: '+1234567892',
        password: 'password123',
        role: 'customer',
        customerInfo: {
          address: {
            street: '456 Oak Ave',
            city: 'Los Angeles',
            state: 'CA',
            zipCode: '90210',
            coordinates: { latitude: 34.0522, longitude: -118.2437 }
          }
        }
      },
      {
        firstName: 'Mike',
        lastName: 'Johnson',
        email: 'mike.johnson@email.com',
        phone: '+1234567893',
        password: 'password123',
        role: 'customer'
      }
    ];

    const createdCustomers = await User.insertMany(customers);
    console.log('✅ Created customer users');

    // Create provider users
    const providers = [
      {
        firstName: 'Robert',
        lastName: 'Wilson',
        email: 'robert.wilson@email.com',
        phone: '+1234567894',
        password: 'password123',
        role: 'provider',
        providerInfo: {
          businessName: 'Wilson Cleaning Services',
          businessAddress: {
            street: '789 Business Blvd',
            city: 'New York',
            state: 'NY',
            zipCode: '10002',
            coordinates: { latitude: 40.7589, longitude: -73.9851 }
          },
          experience: 5,
          rating: { average: 4.8, count: 25 },
          isAvailable: true,
          workingHours: {
            monday: { start: '08:00', end: '18:00', isWorking: true },
            tuesday: { start: '08:00', end: '18:00', isWorking: true },
            wednesday: { start: '08:00', end: '18:00', isWorking: true },
            thursday: { start: '08:00', end: '18:00', isWorking: true },
            friday: { start: '08:00', end: '18:00', isWorking: true },
            saturday: { start: '09:00', end: '16:00', isWorking: true },
            sunday: { start: '10:00', end: '15:00', isWorking: false }
          }
        }
      },
      {
        firstName: 'Sarah',
        lastName: 'Davis',
        email: 'sarah.davis@email.com',
        phone: '+1234567895',
        password: 'password123',
        role: 'provider',
        providerInfo: {
          businessName: 'Davis Plumbing Solutions',
          businessAddress: {
            street: '321 Service St',
            city: 'Los Angeles',
            state: 'CA',
            zipCode: '90211',
            coordinates: { latitude: 34.0736, longitude: -118.4004 }
          },
          experience: 8,
          rating: { average: 4.9, count: 42 },
          isAvailable: true,
          workingHours: {
            monday: { start: '07:00', end: '19:00', isWorking: true },
            tuesday: { start: '07:00', end: '19:00', isWorking: true },
            wednesday: { start: '07:00', end: '19:00', isWorking: true },
            thursday: { start: '07:00', end: '19:00', isWorking: true },
            friday: { start: '07:00', end: '19:00', isWorking: true },
            saturday: { start: '08:00', end: '17:00', isWorking: true },
            sunday: { start: '09:00', end: '16:00', isWorking: true }
          }
        }
      },
      {
        firstName: 'David',
        lastName: 'Brown',
        email: 'david.brown@email.com',
        phone: '+1234567896',
        password: 'password123',
        role: 'provider',
        providerInfo: {
          businessName: 'Brown Electrical Works',
          businessAddress: {
            street: '654 Electric Ave',
            city: 'Chicago',
            state: 'IL',
            zipCode: '60601',
            coordinates: { latitude: 41.8781, longitude: -87.6298 }
          },
          experience: 12,
          rating: { average: 4.7, count: 38 },
          isAvailable: true,
          workingHours: {
            monday: { start: '08:00', end: '17:00', isWorking: true },
            tuesday: { start: '08:00', end: '17:00', isWorking: true },
            wednesday: { start: '08:00', end: '17:00', isWorking: true },
            thursday: { start: '08:00', end: '17:00', isWorking: true },
            friday: { start: '08:00', end: '17:00', isWorking: true },
            saturday: { start: '09:00', end: '15:00', isWorking: true },
            sunday: { start: '10:00', end: '14:00', isWorking: false }
          }
        }
      },
      {
        firstName: 'Lisa',
        lastName: 'Garcia',
        email: 'lisa.garcia@email.com',
        phone: '+1234567897',
        password: 'password123',
        role: 'provider',
        providerInfo: {
          businessName: 'Garcia HVAC Services',
          businessAddress: {
            street: '987 Climate Ct',
            city: 'Houston',
            state: 'TX',
            zipCode: '77001',
            coordinates: { latitude: 29.7604, longitude: -95.3698 }
          },
          experience: 6,
          rating: { average: 4.6, count: 31 },
          isAvailable: true,
          workingHours: {
            monday: { start: '07:30', end: '18:30', isWorking: true },
            tuesday: { start: '07:30', end: '18:30', isWorking: true },
            wednesday: { start: '07:30', end: '18:30', isWorking: true },
            thursday: { start: '07:30', end: '18:30', isWorking: true },
            friday: { start: '07:30', end: '18:30', isWorking: true },
            saturday: { start: '08:00', end: '16:00', isWorking: true },
            sunday: { start: '09:00', end: '15:00', isWorking: true }
          }
        }
      }
    ];

    const createdProviders = await User.insertMany(providers);
    console.log('✅ Created provider users');

    // Create services
    const services = [
      {
        name: 'Deep House Cleaning',
        description: 'Complete deep cleaning of your home including all rooms, bathrooms, kitchen, and common areas. We use eco-friendly products and professional equipment.',
        category: createdCategories[0]._id, // Home Cleaning
        provider: createdProviders[0]._id, // Robert Wilson
        price: 150,
        duration: 180, // 3 hours
        isFeatured: true,
        rating: { average: 4.8, count: 25 },
        location: {
          type: 'at_customer',
          serviceArea: { radius: 15, cities: ['New York', 'Brooklyn', 'Queens'] }
        },
        availability: {
          monday: { start: '08:00', end: '18:00', isAvailable: true },
          tuesday: { start: '08:00', end: '18:00', isAvailable: true },
          wednesday: { start: '08:00', end: '18:00', isAvailable: true },
          thursday: { start: '08:00', end: '18:00', isAvailable: true },
          friday: { start: '08:00', end: '18:00', isAvailable: true },
          saturday: { start: '09:00', end: '16:00', isAvailable: true },
          sunday: { start: '10:00', end: '15:00', isAvailable: false }
        },
        tags: ['cleaning', 'house', 'deep clean', 'eco-friendly']
      },
      {
        name: 'Kitchen Deep Clean',
        description: 'Specialized kitchen cleaning including appliances, cabinets, countertops, and floors. Perfect for move-in/out or regular maintenance.',
        category: createdCategories[0]._id, // Home Cleaning
        provider: createdProviders[0]._id, // Robert Wilson
        price: 80,
        duration: 90, // 1.5 hours
        rating: { average: 4.7, count: 18 },
        location: {
          type: 'at_customer',
          serviceArea: { radius: 15, cities: ['New York', 'Brooklyn', 'Queens'] }
        },
        availability: {
          monday: { start: '08:00', end: '18:00', isAvailable: true },
          tuesday: { start: '08:00', end: '18:00', isAvailable: true },
          wednesday: { start: '08:00', end: '18:00', isAvailable: true },
          thursday: { start: '08:00', end: '18:00', isAvailable: true },
          friday: { start: '08:00', end: '18:00', isAvailable: true },
          saturday: { start: '09:00', end: '16:00', isAvailable: true },
          sunday: { start: '10:00', end: '15:00', isAvailable: false }
        },
        tags: ['cleaning', 'kitchen', 'appliances', 'cabinets']
      },
      {
        name: 'Emergency Plumbing Repair',
        description: '24/7 emergency plumbing services for leaks, clogs, and urgent repairs. Licensed and insured professionals with quick response time.',
        category: createdCategories[1]._id, // Plumbing
        provider: createdProviders[1]._id, // Sarah Davis
        price: 120,
        duration: 60, // 1 hour
        isFeatured: true,
        rating: { average: 4.9, count: 42 },
        location: {
          type: 'at_customer',
          serviceArea: { radius: 25, cities: ['Los Angeles', 'Beverly Hills', 'Santa Monica'] }
        },
        availability: {
          monday: { start: '00:00', end: '23:59', isAvailable: true },
          tuesday: { start: '00:00', end: '23:59', isAvailable: true },
          wednesday: { start: '00:00', end: '23:59', isAvailable: true },
          thursday: { start: '00:00', end: '23:59', isAvailable: true },
          friday: { start: '00:00', end: '23:59', isAvailable: true },
          saturday: { start: '00:00', end: '23:59', isAvailable: true },
          sunday: { start: '00:00', end: '23:59', isAvailable: true }
        },
        tags: ['plumbing', 'emergency', 'repair', 'leak', 'clog']
      },
      {
        name: 'Drain Cleaning Service',
        description: 'Professional drain cleaning and unclogging services using high-pressure water jets and specialized tools.',
        category: createdCategories[1]._id, // Plumbing
        provider: createdProviders[1]._id, // Sarah Davis
        price: 95,
        duration: 75, // 1.25 hours
        rating: { average: 4.8, count: 28 },
        location: {
          type: 'at_customer',
          serviceArea: { radius: 25, cities: ['Los Angeles', 'Beverly Hills', 'Santa Monica'] }
        },
        availability: {
          monday: { start: '07:00', end: '19:00', isAvailable: true },
          tuesday: { start: '07:00', end: '19:00', isAvailable: true },
          wednesday: { start: '07:00', end: '19:00', isAvailable: true },
          thursday: { start: '07:00', end: '19:00', isAvailable: true },
          friday: { start: '07:00', end: '19:00', isAvailable: true },
          saturday: { start: '08:00', end: '17:00', isAvailable: true },
          sunday: { start: '09:00', end: '16:00', isAvailable: true }
        },
        tags: ['plumbing', 'drain', 'cleaning', 'unclog']
      },
      {
        name: 'Electrical Outlet Installation',
        description: 'Professional installation of new electrical outlets, switches, and fixtures. Licensed electrician with safety guarantee.',
        category: createdCategories[2]._id, // Electrical
        provider: createdProviders[2]._id, // David Brown
        price: 85,
        duration: 45, // 45 minutes
        isFeatured: true,
        rating: { average: 4.7, count: 38 },
        location: {
          type: 'at_customer',
          serviceArea: { radius: 20, cities: ['Chicago', 'Evanston', 'Oak Park'] }
        },
        availability: {
          monday: { start: '08:00', end: '17:00', isAvailable: true },
          tuesday: { start: '08:00', end: '17:00', isAvailable: true },
          wednesday: { start: '08:00', end: '17:00', isAvailable: true },
          thursday: { start: '08:00', end: '17:00', isAvailable: true },
          friday: { start: '08:00', end: '17:00', isAvailable: true },
          saturday: { start: '09:00', end: '15:00', isAvailable: true },
          sunday: { start: '10:00', end: '14:00', isAvailable: false }
        },
        tags: ['electrical', 'outlet', 'installation', 'switch']
      },
      {
        name: 'AC Repair & Maintenance',
        description: 'Complete air conditioning repair, maintenance, and tune-up services. Includes filter replacement and system inspection.',
        category: createdCategories[3]._id, // HVAC
        provider: createdProviders[3]._id, // Lisa Garcia
        price: 110,
        duration: 90, // 1.5 hours
        rating: { average: 4.6, count: 31 },
        location: {
          type: 'at_customer',
          serviceArea: { radius: 30, cities: ['Houston', 'Sugar Land', 'Katy'] }
        },
        availability: {
          monday: { start: '07:30', end: '18:30', isAvailable: true },
          tuesday: { start: '07:30', end: '18:30', isAvailable: true },
          wednesday: { start: '07:30', end: '18:30', isAvailable: true },
          thursday: { start: '07:30', end: '18:30', isAvailable: true },
          friday: { start: '07:30', end: '18:30', isAvailable: true },
          saturday: { start: '08:00', end: '16:00', isAvailable: true },
          sunday: { start: '09:00', end: '15:00', isAvailable: true }
        },
        tags: ['hvac', 'ac', 'repair', 'maintenance', 'cooling']
      }
    ];

    const createdServices = await Service.insertMany(services);
    console.log('✅ Created services');

    // Update providers with their services
    await User.findByIdAndUpdate(createdProviders[0]._id, {
      'providerInfo.services': [createdServices[0]._id, createdServices[1]._id]
    });
    await User.findByIdAndUpdate(createdProviders[1]._id, {
      'providerInfo.services': [createdServices[2]._id, createdServices[3]._id]
    });
    await User.findByIdAndUpdate(createdProviders[2]._id, {
      'providerInfo.services': [createdServices[4]._id]
    });
    await User.findByIdAndUpdate(createdProviders[3]._id, {
      'providerInfo.services': [createdServices[5]._id]
    });

    // Create sample bookings
    const bookings = [
      {
        customer: createdCustomers[0]._id,
        provider: createdProviders[0]._id,
        service: createdServices[0]._id,
        bookingDate: new Date('2024-01-15'),
        startTime: '10:00',
        endTime: '13:00',
        status: 'completed',
        location: {
          type: 'at_customer',
          address: {
            street: '123 Main St',
            city: 'New York',
            state: 'NY',
            zipCode: '10001',
            coordinates: { latitude: 40.7128, longitude: -74.0060 }
          }
        },
        payment: {
          amount: 150,
          status: 'paid',
          method: 'stripe',
          paidAt: new Date('2024-01-15T09:30:00Z')
        },
        rating: {
          customerRating: {
            rating: 5,
            review: 'Excellent service! Very thorough and professional.',
            ratedAt: new Date('2024-01-15T14:00:00Z')
          }
        }
      },
      {
        customer: createdCustomers[1]._id,
        provider: createdProviders[1]._id,
        service: createdServices[2]._id,
        bookingDate: new Date('2024-01-16'),
        startTime: '14:00',
        endTime: '15:00',
        status: 'completed',
        location: {
          type: 'at_customer',
          address: {
            street: '456 Oak Ave',
            city: 'Los Angeles',
            state: 'CA',
            zipCode: '90210',
            coordinates: { latitude: 34.0522, longitude: -118.2437 }
          }
        },
        payment: {
          amount: 120,
          status: 'paid',
          method: 'stripe',
          paidAt: new Date('2024-01-16T13:30:00Z')
        },
        rating: {
          customerRating: {
            rating: 5,
            review: 'Quick response and fixed the issue immediately!',
            ratedAt: new Date('2024-01-16T15:30:00Z')
          }
        }
      },
      {
        customer: createdCustomers[0]._id,
        provider: createdProviders[2]._id,
        service: createdServices[4]._id,
        bookingDate: new Date('2024-01-20'),
        startTime: '11:00',
        endTime: '11:45',
        status: 'confirmed',
        location: {
          type: 'at_customer',
          address: {
            street: '123 Main St',
            city: 'New York',
            state: 'NY',
            zipCode: '10001',
            coordinates: { latitude: 40.7128, longitude: -74.0060 }
          }
        },
        payment: {
          amount: 85,
          status: 'pending',
          method: 'stripe'
        }
      }
    ];

    await Booking.insertMany(bookings);
    console.log('✅ Created sample bookings');

    console.log('🎉 Data seeding completed successfully!');
    console.log('\n📊 Summary:');
    console.log(`- ${createdCategories.length} service categories`);
    console.log(`- ${createdCustomers.length} customer users`);
    console.log(`- ${createdProviders.length} provider users`);
    console.log(`- ${createdServices.length} services`);
    console.log(`- ${bookings.length} sample bookings`);
    console.log('\n🔑 Test Credentials:');
    console.log('Admin: admin@easybook.com / admin123');
    console.log('Customer: john.doe@email.com / password123');
    console.log('Provider: robert.wilson@email.com / password123');

  } catch (error) {
    console.error('❌ Error seeding data:', error);
  } finally {
    mongoose.connection.close();
  }
};

// Run the seeding function
seedData();
