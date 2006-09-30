package org.mifos.application.bulkentry.persistance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.bulkentry.business.BulkEntryClientAttendanceView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestBulkEntryPersistance extends MifosTestCase {

	private AccountPersistence accountPersistence;

	private CustomerBO center;

	private CustomerBO group;

	private CustomerBO client;

	private AccountBO account;
    
    private ClientAttendanceBO clientAttendance;
    
    private BulkEntryPersistance bulkEntryPersistance;
    
    private CustomerPersistence customerPersistence;
    

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		accountPersistence = new AccountPersistence();
        bulkEntryPersistance = new BulkEntryPersistance();  
        customerPersistence = new CustomerPersistence();  
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(account);
        //TestObjectFactory.deleteClientAttendence(client);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		super.tearDown();
		HibernateUtil.closeSession();
	}

	public void testGetAccount() throws Exception{

		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		account = TestObjectFactory.createLoanAccount("42423142341", group,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
		HibernateUtil.closeSession();
		account = accountPersistence.getAccount(account.getAccountId());
		assertEquals(((LoanBO) account).getLoanOffering().getPrdOfferingName(),
				"Loan");
	}

	public void testSuccessfulApplyPayment() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		account = TestObjectFactory.createLoanAccount("42423142341", group,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
		HibernateUtil.closeSession();
		account = accountPersistence.getAccount(account.getAccountId());
		assertEquals(((LoanBO) account).getLoanOffering().getPrdOfferingName(),
				"Loan");

		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.add(account.getAccountActionDates().iterator().next());
		Date currentDate = new Date(System.currentTimeMillis());
		PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(
				accntActionDates, new Money(TestObjectFactory.getMFICurrency(),
						"100.0"), null, account.getPersonnel(), "423423",
				Short.valueOf("1"), currentDate, currentDate);
		try {
			account.applyPayment(paymentData);
			assertEquals(((LoanBO) account).getLoanSummary().getFeesPaid()
					.getAmountDoubleValue(), Double.valueOf("100.0"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		account.getAccountPayments().clear();

	}

	public void testSuccessfulLoanUpdate() throws Exception {

		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		account = TestObjectFactory.createLoanAccount("42423142341", group,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
		HibernateUtil.closeSession();
		account = accountPersistence.getAccount(account.getAccountId());
		assertEquals(((LoanBO) account).getLoanOffering().getPrdOfferingName(),
				"Loan");

		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.add(account.getAccountActionDates().iterator().next());
		Date currentDate = new Date(System.currentTimeMillis());
		PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(
				accntActionDates, new Money(TestObjectFactory.getMFICurrency(),
						"100.0"), null, account.getPersonnel(), "423423",
				Short.valueOf("1"), currentDate, currentDate);

		account.applyPayment(paymentData);
		HibernateUtil.commitTransaction();
		assertEquals(((LoanBO) account).getLoanSummary().getFeesPaid()
				.getAmountDoubleValue(), Double.valueOf("100.0"));
	}

	public void testFailureApplyPayment() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		account = TestObjectFactory.createLoanAccount("42423142341", group,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
		HibernateUtil.closeSession();
		account = accountPersistence.getAccount(account.getAccountId());
		assertEquals(((LoanBO) account).getLoanOffering().getPrdOfferingName(),
				"Loan");
		for (AccountActionDateEntity actionDate : account
				.getAccountActionDates()) {
			if (actionDate.getInstallmentId().equals(Short.valueOf("1"))) {
				actionDate.setPaymentStatus(PaymentStatus.PAID.getValue());
			}
		}
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.addAll(account.getAccountActionDates());
		Date currentDate = new Date(System.currentTimeMillis());
		PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(
				accntActionDates, new Money(TestObjectFactory.getMFICurrency(),
						"3000.0"), null, account.getPersonnel(), "423423",
				Short.valueOf("1"), currentDate, currentDate);

		try {
			account.applyPayment(paymentData);
			assertTrue(false);
		} catch (AccountException be) {
			assertNotNull(be);
			assertEquals(be.getKey(), "errors.update");
			assertTrue(true);
		}
		
	}
       
    public void testGetBulkEntryClientAttendanceActionView() throws PersistenceException{
        
        createInitialObjects();
        java.util.Date meetingDate = DateUtils.getCurrentDateWithoutTimeStamp();
        
        clientAttendance = new ClientAttendanceBO();
        clientAttendance.setAttendance(new Short("1"));
        clientAttendance.setMeetingDate(meetingDate);
        ((ClientBO)client).addClientAttendance(clientAttendance );
        customerPersistence.createOrUpdate(client);
        HibernateUtil.commitTransaction();
        HibernateUtil.closeSession();
        
        List<BulkEntryClientAttendanceView> bulkEntryClientAttendanceView = 
            bulkEntryPersistance.getBulkEntryClientAttendanceActionView(
                meetingDate, client.getOffice().getOfficeId());    
        
        assertEquals(bulkEntryClientAttendanceView.size(),1);
        
    }
    
    private void createInitialObjects() {
        
            MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
        
                .getMeetingHelper(1, 1, 4, 2));
            center = TestObjectFactory.createCenter("Center", CustomerStatus.CENTER_ACTIVE.getValue(),
                    "1.1", meeting, new Date(System.currentTimeMillis()));
            group = TestObjectFactory.createGroup("Group", CustomerStatus.GROUP_ACTIVE.getValue(),
                    "1.1.1", center, new Date(System.currentTimeMillis()));
            client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE.getValue(),
                    "1.1.1.1", group, new Date(System.currentTimeMillis()));
                        
            HibernateUtil.closeSession();
        
        
    }
}
