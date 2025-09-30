const axios = require('axios');

class MapsService {
  constructor() {
    this.apiKey = process.env.GOOGLE_MAPS_API_KEY;
    this.baseUrl = 'https://maps.googleapis.com/maps/api';
  }

  async getDirections({ origin, destination, mode = 'driving', avoid = [] }) {
    try {
      const params = {
        origin,
        destination,
        mode,
        key: this.apiKey
      };

      if (avoid.length > 0) {
        params.avoid = avoid.join('|');
      }

      const response = await axios.get(`${this.baseUrl}/directions/json`, { params });
      
      if (response.data.status !== 'OK') {
        throw new Error(`Google Maps API error: ${response.data.status}`);
      }

      const route = response.data.routes[0];
      const leg = route.legs[0];

      return {
        distance: leg.distance,
        duration: leg.duration,
        steps: leg.steps.map(step => ({
          instruction: step.html_instructions,
          distance: step.distance,
          duration: step.duration,
          startLocation: step.start_location,
          endLocation: step.end_location
        })),
        overviewPolyline: route.overview_polyline.points
      };
    } catch (error) {
      console.error('Get directions error:', error);
      throw error;
    }
  }

  async getDistance({ origin, destination, mode = 'driving' }) {
    try {
      const params = {
        origins: origin,
        destinations: destination,
        mode,
        key: this.apiKey
      };

      const response = await axios.get(`${this.baseUrl}/distancematrix/json`, { params });
      
      if (response.data.status !== 'OK') {
        throw new Error(`Google Maps API error: ${response.data.status}`);
      }

      const element = response.data.rows[0].elements[0];
      
      if (element.status !== 'OK') {
        throw new Error(`Distance calculation failed: ${element.status}`);
      }

      return {
        distance: element.distance,
        duration: element.duration
      };
    } catch (error) {
      console.error('Get distance error:', error);
      throw error;
    }
  }

  async getGeocode(address) {
    try {
      const params = {
        address,
        key: this.apiKey
      };

      const response = await axios.get(`${this.baseUrl}/geocode/json`, { params });
      
      if (response.data.status !== 'OK') {
        throw new Error(`Google Maps API error: ${response.data.status}`);
      }

      if (response.data.results.length === 0) {
        throw new Error('No results found for the given address');
      }

      const result = response.data.results[0];
      
      return {
        formattedAddress: result.formatted_address,
        location: result.geometry.location,
        placeId: result.place_id,
        addressComponents: result.address_components
      };
    } catch (error) {
      console.error('Get geocode error:', error);
      throw error;
    }
  }
}

module.exports = new MapsService();
