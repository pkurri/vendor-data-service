import { VendorDataClient } from '@vendor/data-client';

/**
 * Basic usage example for the Vendor Data Service TypeScript client
 */
async function main() {
  // Configure the client
  const client = new VendorDataClient({
    baseUrl: 'https://api.vendor.com',
    clientId: 'your-client-id',
    clientSecret: 'your-client-secret',
    tokenUrl: 'https://auth.vendor.com/oauth/token',
    scope: 'vendor.search',
    debug: true,
  });

  try {
    // Check service health
    const isHealthy = await client.healthCheck();
    if (!isHealthy) {
      console.error('Service is not healthy!');
      return;
    }
    console.log('✓ Service is healthy');

    // Example 1: Search by name and DOB
    console.log('\n--- Example 1: Search by name and DOB ---');
    const result1 = await client.search({
      firstName: 'John',
      lastName: 'Doe',
      dateOfBirth: '1990-01-01',
      page: 1,
      pageSize: 50,
    });
    console.log(`Found ${result1.totalRecords} records`);
    console.log(`First case: ${result1.data[0]?.caseNumber}`);

    // Example 2: Search by case number
    console.log('\n--- Example 2: Search by case number ---');
    const result2 = await client.search({
      caseNumber: 'CR-2024-12345',
      includeCharges: true,
      includeSentences: true,
    });
    result2.data.forEach((caseRecord) => {
      console.log(`Case: ${caseRecord.caseNumber}`);
      console.log(`County: ${caseRecord.county}`);
      console.log(`Status: ${caseRecord.caseStatus}`);
      console.log(`Charges: ${caseRecord.charges?.length || 0}`);
    });

    // Example 3: Search with date range
    console.log('\n--- Example 3: Search with date range ---');
    const result3 = await client.search({
      lastName: 'Smith',
      filingDateFrom: '2024-01-01',
      filingDateTo: '2024-12-31',
      county: 'MARION',
      page: 1,
      pageSize: 100,
    });
    console.log(`Cases filed in 2024: ${result3.totalRecords}`);

    // Example 4: Using GET method
    console.log('\n--- Example 4: Using GET method ---');
    const result4 = await client.searchGet({
      firstName: 'Jane',
      lastName: 'Smith',
    });
    console.log(`Found ${result4.totalRecords} records using GET`);

  } catch (error) {
    console.error('Error:', error);
  }
}

// Run the example
main().catch(console.error);
