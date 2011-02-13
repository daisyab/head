/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.test.acceptance.loan;

import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"loan","acceptance","ui","smoke"})
public class LoanAccountPerformanceHistoryTest extends UiTestCaseBase {
/*
    private static final String CLIENT_PERFORMANCE_HISTORY = "CLIENT_PERF_HISTORY";
    private AppLauncher appLauncher;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        appLauncher = new AppLauncher(selenium);

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,6,25,8,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void repayLoanAndVerifyPerformanceHistory() throws Exception {

        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);

        // find the loan w/ id 000100000000048
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();
        SearchResultsPage searchResults = homePage.search("000100000000048");
        searchResults.verifyPage();
        LoanAccountPage loanAccountPage = searchResults.navigateToLoanAccountDetailPage("000100000000048");

        // this loan is already disbursed. We repay it and make sure that the performance history is correct.
        RepayLoanParameters params = new RepayLoanParameters();
        params.setModeOfRepayment(RepayLoanParameters.CASH);

        RepayLoanPage repayLoanPage = loanAccountPage.navigateToRepayLoan();
        RepayLoanConfirmationPage repayLoanConfirmationPage = repayLoanPage.submitAndNavigateToRepayLoanConfirmationPage(params);
        repayLoanConfirmationPage.submitAndNavigateToLoanAccountDetailsPage();

        // validate
        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile("LoanAccountPerformanceHistoryTest_001_result_dbunit.xml");
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, new String[] { CLIENT_PERFORMANCE_HISTORY });

        dbUnitUtilities.verifyTable(CLIENT_PERFORMANCE_HISTORY, databaseDataSet, expectedDataSet);
    }
*/
}
