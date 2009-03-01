package org.mifos.application.bulkentry.persistance.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.AttendanceType;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.service.ClientAttendanceDto;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestBulkEntryPersistenceService extends MifosTestCase {

	public TestBulkEntryPersistenceService() throws SystemException, ApplicationException {
        super();
    }

    private CustomerBO center;

	private CustomerBO group;

	private CustomerBO client;

	private AccountBO account;

    private ClientAttendanceBO clientAttendance;

    private AccountPersistence accountPersistence;
    
    private BulkEntryPersistenceService bulkEntryPersistanceService;
    
    private CustomerPersistence customerPersistence;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		accountPersistence = new AccountPersistence();
		bulkEntryPersistanceService = new BulkEntryPersistenceService();
		customerPersistence = new CustomerPersistence();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(account);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		super.tearDown();
		HibernateUtil.closeSession();
	}

	public void testGetAccount() throws Exception{
		Date startDate = new Date(System.currentTimeMillis());

		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center_Active", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		account = TestObjectFactory.createLoanAccount("42423142341", group,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate,
				loanOffering);
		HibernateUtil.closeSession();
		account = accountPersistence.getAccount(account.getAccountId());
		assertEquals(((LoanBO) account).getLoanOffering().getPrdOfferingName(),
				"Loan");
	}

	public void testSuccessfulLoanUpdate() throws Exception {
		Date startDate = new Date(System.currentTimeMillis());

		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center_Active", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		account = TestObjectFactory.createLoanAccount("42423142341", group,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate,
				loanOffering);
		HibernateUtil.closeSession();
		account = accountPersistence.getAccount(account.getAccountId());
		assertEquals(((LoanBO) account).getLoanOffering().getPrdOfferingName(),
				"Loan");

		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.add(account.getAccountActionDates().iterator().next());
		Date currentDate = startDate;
		PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(
				accntActionDates, new Money(TestObjectFactory.getMFICurrency(),
						"100.0"), null, account.getPersonnel(), "423423", Short
						.valueOf("1"), currentDate, currentDate);

		account.applyPaymentWithPersist(paymentData);
		HibernateUtil.commitTransaction();
		assertEquals(((LoanBO) account).getLoanSummary().getFeesPaid()
				.getAmountDoubleValue(), Double.valueOf("100.0"));
	}
    
public void testGetBulkEntryClientAttendanceActionView() throws PersistenceException{
        
        createInitialObjects();
        java.util.Date meetingDate = DateUtils.getCurrentDateWithoutTimeStamp();
        
        clientAttendance = new ClientAttendanceBO();
        clientAttendance.setAttendance(AttendanceType.PRESENT);
        clientAttendance.setMeetingDate(meetingDate);
        ((ClientBO)client).addClientAttendance(clientAttendance );
        customerPersistence.createOrUpdate(client);
        HibernateUtil.commitTransaction();
        HibernateUtil.closeSession();
        
        List<ClientAttendanceDto> collectionSheetEntryClientAttendanceView = 
            bulkEntryPersistanceService.getBulkEntryClientAttendanceActionView(
                meetingDate, client.getOffice().getOfficeId());
        assertEquals(collectionSheetEntryClientAttendanceView.size(),1);
    }
    
    private void createInitialObjects() {
            MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
        
                .getTypicalMeeting());
            center = TestObjectFactory.createCenter("Center", meeting);
            group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
            client = TestObjectFactory.createClient("Client", 
            		CustomerStatus.CLIENT_ACTIVE,
                    group);
                        
            HibernateUtil.closeSession();
    }

}
