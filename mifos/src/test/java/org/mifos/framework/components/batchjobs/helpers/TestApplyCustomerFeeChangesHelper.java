package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerFeeScheduleEntity;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.business.TestCustomerAccountBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeChangeType;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestApplyCustomerFeeChangesHelper extends MifosTestCase {
	public TestApplyCustomerFeeChangesHelper() throws SystemException, ApplicationException {
        super();
    }

    private static final double DELTA = 0.00000001;

    CustomerBO center = null;

	CustomerBO group = null;

	@Override
	protected void setUp() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("center1", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			List<CustomerBO> customerList = new ArrayList<CustomerBO>();
			if (group != null)
				customerList.add(group);
			if (center != null)
				customerList.add(center);
			TestObjectFactory.cleanUp(customerList);
		} catch (Exception e) {
			// TODO Whoops, cleanup didnt work, reset db
			TestDatabase.resetMySQLDatabase();
		}
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testExecuteAmountUpdated() throws Exception {
		CustomerAccountBO customerAccount = center.getCustomerAccount();
		Set<AccountFeesEntity> accountFeeSet = customerAccount.getAccountFees();
		FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee(
				"Training_Fee", FeeCategory.ALLCUSTOMERS, "10",
				RecurrenceType.WEEKLY, Short.valueOf("2"));
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(center
				.getCustomerAccount(), trainingFee, new Double("10.0"));
		accountFeeSet.add(accountPeriodicFee);
		CustomerScheduleEntity accountActionDate = (CustomerScheduleEntity) customerAccount
				.getAccountActionDate(Short.valueOf("1"));
		AccountFeesActionDetailEntity accountFeesaction = new CustomerFeeScheduleEntity(
				accountActionDate, trainingFee, accountPeriodicFee, new Money(
						"10.0"));
		TestCustomerAccountBO.setFeeAmountPaid((CustomerFeeScheduleEntity) accountFeesaction,new Money("0.0"));
		accountActionDate.addAccountFeesAction(accountFeesaction);
		TestObjectFactory.flushandCloseSession();

		trainingFee = (FeeBO) HibernateUtil.getSessionTL().get(FeeBO.class,
				trainingFee.getFeeId());
		trainingFee.setUserContext(TestUtils.makeUser());
		((AmountFeeBO) trainingFee).setFeeAmount(TestObjectFactory
				.getMoneyForMFICurrency("5"));
		trainingFee.updateFeeChangeType(FeeChangeType.AMOUNT_UPDATED);
		trainingFee.save();
		TestObjectFactory.flushandCloseSession();
		ApplyCustomerFeeChangesTask task = new ApplyCustomerFeeChangesTask();
		((ApplyCustomerFeeChangesHelper) task
				.getTaskHelper()).execute(System.currentTimeMillis());
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO) HibernateUtil.getSessionTL().get(
				CustomerBO.class, center.getCustomerId());

		CustomerScheduleEntity installment = (CustomerScheduleEntity) center
				.getCustomerAccount().getAccountActionDate(Short.valueOf("1"));

		AccountFeesActionDetailEntity accountFeesAction = installment
				.getAccountFeesAction(accountPeriodicFee.getAccountFeeId());
		assertEquals(5.0, accountFeesAction.getFeeAmount().getAmountDoubleValue(), DELTA);
		HibernateUtil.closeSession();
		center = (CustomerBO) HibernateUtil.getSessionTL().get(
				CustomerBO.class, center.getCustomerId());
		group = (CustomerBO) HibernateUtil.getSessionTL().get(CustomerBO.class,
				group.getCustomerId());
	}

	public void testExecuteStatusUpdatedToInactive() throws Exception {
		CustomerAccountBO customerAccount = center.getCustomerAccount();
		CustomerScheduleEntity accountActionDate = (CustomerScheduleEntity) customerAccount
		.getAccountActionDate(Short.valueOf("1"));
		TestCustomerAccountBO.setActionDate(accountActionDate,offSetDate(accountActionDate.getActionDate(),-1));
		TestObjectFactory.updateObject(center);
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO) HibernateUtil.getSessionTL().get(
				CustomerBO.class, center.getCustomerId());
		customerAccount = center.getCustomerAccount();
		Set<AccountFeesEntity> accountFeeSet = customerAccount.getAccountFees();
		FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee(
				"Training_Fee", FeeCategory.ALLCUSTOMERS, "10",
				RecurrenceType.WEEKLY, Short.valueOf("2"));
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(center
				.getCustomerAccount(), trainingFee, new Double("10.0"));
		accountFeeSet.add(accountPeriodicFee);
		accountActionDate = (CustomerScheduleEntity) customerAccount
				.getAccountActionDate(Short.valueOf("2"));
		AccountFeesActionDetailEntity accountFeesaction = new CustomerFeeScheduleEntity(
				accountActionDate, trainingFee, accountPeriodicFee, new Money(
						"10.0"));
		TestCustomerAccountBO.setFeeAmountPaid((CustomerFeeScheduleEntity) accountFeesaction,new Money("0.0"));
		accountActionDate.addAccountFeesAction(accountFeesaction);
		TestObjectFactory.flushandCloseSession();

		trainingFee = (FeeBO) HibernateUtil.getSessionTL().get(FeeBO.class,
				trainingFee.getFeeId());
		trainingFee.setUserContext(TestUtils.makeUserWithLocales());
		trainingFee.updateFeeChangeType(FeeChangeType.STATUS_UPDATED);
		trainingFee.updateStatus(FeeStatus.INACTIVE);

		trainingFee.update();
		TestObjectFactory.flushandCloseSession();
		ApplyCustomerFeeChangesTask applyCustomerFeeChangesTask = new ApplyCustomerFeeChangesTask();
		((ApplyCustomerFeeChangesHelper) applyCustomerFeeChangesTask
				.getTaskHelper()).execute(System.currentTimeMillis());
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO) HibernateUtil.getSessionTL().get(
				CustomerBO.class, center.getCustomerId());

		AccountActionDateEntity installment = center.getCustomerAccount()
				.getAccountActionDate(Short.valueOf("2"));

		AccountFeesActionDetailEntity accountFeesAction = ((CustomerScheduleEntity) installment)
				.getAccountFeesAction(accountPeriodicFee.getAccountFeeId());
		assertNull(accountFeesAction);
	}

	public void testExecuteStatusInactiveAndAmountUpdated() throws Exception {
		CustomerAccountBO customerAccount = center.getCustomerAccount();
		CustomerScheduleEntity accountActionDate = (CustomerScheduleEntity) customerAccount
		.getAccountActionDate(Short.valueOf("1"));
		TestCustomerAccountBO.setActionDate(accountActionDate,offSetDate(accountActionDate.getActionDate(),-1));
		TestObjectFactory.updateObject(center);
		HibernateUtil.closeSession();
		
		center = (CustomerBO) HibernateUtil.getSessionTL().get(
				CustomerBO.class, center.getCustomerId());
		customerAccount = center.getCustomerAccount();
		Set<AccountFeesEntity> accountFeeSet = customerAccount.getAccountFees();
		FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee(
				"Training_Fee", FeeCategory.ALLCUSTOMERS, "10",
				RecurrenceType.WEEKLY, Short.valueOf("2"));
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(center
				.getCustomerAccount(), trainingFee, ((AmountFeeBO) trainingFee)
				.getFeeAmount().getAmountDoubleValue());
		accountFeeSet.add(accountPeriodicFee);
		accountActionDate = (CustomerScheduleEntity) customerAccount
				.getAccountActionDate(Short.valueOf("2"));
		AccountFeesActionDetailEntity accountFeesaction = new CustomerFeeScheduleEntity(
				accountActionDate, trainingFee, accountPeriodicFee, new Money(
						"10.0"));
		TestCustomerAccountBO.setFeeAmountPaid((CustomerFeeScheduleEntity) accountFeesaction,new Money("0.0"));
		accountActionDate.addAccountFeesAction(accountFeesaction);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		trainingFee = (FeeBO) HibernateUtil.getSessionTL().get(FeeBO.class,
				trainingFee.getFeeId());
		trainingFee.setUserContext(TestUtils.makeUserWithLocales());
		trainingFee
				.updateFeeChangeType(FeeChangeType.AMOUNT_AND_STATUS_UPDATED);
		((AmountFeeBO) trainingFee).setFeeAmount(TestObjectFactory
				.getMoneyForMFICurrency("5"));
		trainingFee.updateStatus(FeeStatus.INACTIVE);
		trainingFee.update();
		TestObjectFactory.flushandCloseSession();
		ApplyCustomerFeeChangesTask applyCustomerFeeChangesTask = new ApplyCustomerFeeChangesTask();
		((ApplyCustomerFeeChangesHelper) applyCustomerFeeChangesTask
				.getTaskHelper()).execute(System.currentTimeMillis());
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO) HibernateUtil.getSessionTL().get(
				CustomerBO.class, center.getCustomerId());

		AccountActionDateEntity installment = center.getCustomerAccount()
				.getAccountActionDate(Short.valueOf("2"));

		AccountFeesActionDetailEntity accountFeesAction = ((CustomerScheduleEntity) installment)
				.getAccountFeesAction(accountPeriodicFee.getAccountFeeId());
		assertNull(accountFeesAction);

		AccountFeesEntity accountFee = center.getCustomerAccount()
				.getAccountFees(trainingFee.getFeeId());

		assertNotNull(accountFee);
		assertEquals(5.0, accountFee.getAccountFeeAmount().getAmountDoubleValue(), DELTA);
	}

	public void testExecuteStatusInactiveToActive() throws Exception {
		CustomerAccountBO customerAccount = center.getCustomerAccount();
		Set<AccountFeesEntity> accountFeeSet = customerAccount.getAccountFees();
		FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee(
				"Training_Fee", FeeCategory.ALLCUSTOMERS, "10",
				RecurrenceType.WEEKLY, Short.valueOf("2"));
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(center
				.getCustomerAccount(), trainingFee, ((AmountFeeBO) trainingFee)
				.getFeeAmount().getAmountDoubleValue());
		accountPeriodicFee.setFeeStatus(FeeStatus.INACTIVE);
		accountFeeSet.add(accountPeriodicFee);
		trainingFee.updateStatus(FeeStatus.INACTIVE);
		trainingFee.setUserContext(TestUtils.makeUserWithLocales());
		trainingFee.update();
		TestObjectFactory.flushandCloseSession();

		trainingFee = (FeeBO) HibernateUtil.getSessionTL().get(FeeBO.class,
				trainingFee.getFeeId());
		trainingFee.setUserContext(TestUtils.makeUserWithLocales());
		trainingFee.updateFeeChangeType(FeeChangeType.STATUS_UPDATED);
		trainingFee.updateStatus(FeeStatus.ACTIVE);
		trainingFee.update();
		TestObjectFactory.flushandCloseSession();
		ApplyCustomerFeeChangesTask applyCustomerFeeChangesTask = new ApplyCustomerFeeChangesTask();
		((ApplyCustomerFeeChangesHelper) applyCustomerFeeChangesTask
				.getTaskHelper()).execute(System.currentTimeMillis());
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO) HibernateUtil.getSessionTL().get(
				CustomerBO.class, center.getCustomerId());

		AccountActionDateEntity installment = center.getCustomerAccount()
				.getAccountActionDate(Short.valueOf("1"));

		AccountFeesActionDetailEntity accountFeesAction = ((CustomerScheduleEntity) installment)
				.getAccountFeesAction(accountPeriodicFee.getAccountFeeId());
		assertNotNull(accountFeesAction);
		assertEquals(2, ((CustomerScheduleEntity) installment)
				.getAccountFeesActionDetails().size());
	}

	public void testExecuteStatusInactiveToActiveAndAmountChanged()
			throws Exception {
		CustomerAccountBO customerAccount = center.getCustomerAccount();
		Set<AccountFeesEntity> accountFeeSet = customerAccount.getAccountFees();
		FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee(
				"Training_Fee", FeeCategory.ALLCUSTOMERS, "10",
				RecurrenceType.WEEKLY, Short.valueOf("2"));
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(center
				.getCustomerAccount(), trainingFee, ((AmountFeeBO) trainingFee)
				.getFeeAmount().getAmountDoubleValue());
		accountPeriodicFee.setFeeStatus(FeeStatus.INACTIVE);
		accountFeeSet.add(accountPeriodicFee);
		trainingFee.updateStatus(FeeStatus.INACTIVE);
		trainingFee.setUserContext(TestUtils.makeUserWithLocales());
		trainingFee.update();
		TestObjectFactory.flushandCloseSession();

		trainingFee = (FeeBO) HibernateUtil.getSessionTL().get(FeeBO.class,
				trainingFee.getFeeId());
		trainingFee.setUserContext(TestUtils.makeUserWithLocales());
		trainingFee
				.updateFeeChangeType(FeeChangeType.AMOUNT_AND_STATUS_UPDATED);
		trainingFee.updateStatus(FeeStatus.ACTIVE);
		((AmountFeeBO) trainingFee).setFeeAmount(TestObjectFactory
				.getMoneyForMFICurrency("5"));
		trainingFee.update();
		TestObjectFactory.flushandCloseSession();
		new ApplyCustomerFeeChangesHelper(new ApplyCustomerFeeChangesTask())
				.execute(System.currentTimeMillis());
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO) HibernateUtil.getSessionTL().get(
				CustomerBO.class, center.getCustomerId());

		AccountActionDateEntity installment = center.getCustomerAccount()
				.getAccountActionDate(Short.valueOf("1"));

		AccountFeesActionDetailEntity accountFeesAction = ((CustomerScheduleEntity) installment)
				.getAccountFeesAction(accountPeriodicFee.getAccountFeeId());
		assertNotNull(accountFeesAction);
		assertEquals(5.0, accountFeesAction.getFeeAmount().getAmountDoubleValue(), DELTA);
		assertEquals(2, ((CustomerScheduleEntity) installment)
				.getAccountFeesActionDetails().size());
		AccountFeesEntity accountFee = center.getCustomerAccount()
				.getAccountFees(trainingFee.getFeeId());
		assertNotNull(accountFee);
		assertEquals(5.0, accountFee.getAccountFeeAmount().getAmountDoubleValue(), DELTA);
	}

	public void testExecuteAmountUpdatedForMultipleAccount() throws Exception {
		CustomerAccountBO centerAccount = center.getCustomerAccount();
		CustomerAccountBO groupAccount = group.getCustomerAccount();
		Set<AccountFeesEntity> accountFeeSet = centerAccount.getAccountFees();
		Set<AccountFeesEntity> groupFeeSet = groupAccount.getAccountFees();

		FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee(
				"Training_Fee", FeeCategory.ALLCUSTOMERS, "10",
				RecurrenceType.WEEKLY, Short.valueOf("2"));

		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(center
				.getCustomerAccount(), trainingFee, ((AmountFeeBO) trainingFee)
				.getFeeAmount().getAmountDoubleValue());

		AccountFeesEntity groupaccountPeriodicFee = new AccountFeesEntity(group
				.getCustomerAccount(), trainingFee, ((AmountFeeBO) trainingFee)
				.getFeeAmount().getAmountDoubleValue());
		accountFeeSet.add(accountPeriodicFee);
		groupFeeSet.add(groupaccountPeriodicFee);
		CustomerScheduleEntity accountActionDate = (CustomerScheduleEntity) centerAccount
				.getAccountActionDate(Short.valueOf("1"));
		AccountFeesActionDetailEntity accountFeesaction = new CustomerFeeScheduleEntity(
				accountActionDate, trainingFee, accountPeriodicFee, new Money(
						"10.0"));
		TestCustomerAccountBO.setFeeAmountPaid((CustomerFeeScheduleEntity) accountFeesaction,new Money("0.0"));
		accountActionDate.addAccountFeesAction(accountFeesaction);
		CustomerScheduleEntity groupaccountActionDate = (CustomerScheduleEntity) groupAccount
				.getAccountActionDate(Short.valueOf("1"));
		AccountFeesActionDetailEntity groupaccountFeesaction = new CustomerFeeScheduleEntity(
				groupaccountActionDate, trainingFee, groupaccountPeriodicFee,
				new Money("10.0"));
		TestCustomerAccountBO.setFeeAmountPaid((CustomerFeeScheduleEntity) groupaccountFeesaction,new Money("0.0"));
		groupaccountActionDate.addAccountFeesAction(groupaccountFeesaction);

		TestObjectFactory.flushandCloseSession();

		trainingFee = (FeeBO) HibernateUtil.getSessionTL().get(FeeBO.class,
				trainingFee.getFeeId());
		trainingFee.setUserContext(TestUtils.makeUser());
		((AmountFeeBO) trainingFee).setFeeAmount(TestObjectFactory
				.getMoneyForMFICurrency("5"));
		trainingFee.updateFeeChangeType(FeeChangeType.AMOUNT_UPDATED);
		trainingFee.save();
		TestObjectFactory.flushandCloseSession();
		new ApplyCustomerFeeChangesHelper(new ApplyCustomerFeeChangesTask())
				.execute(System.currentTimeMillis());
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO) HibernateUtil.getSessionTL().get(
				CustomerBO.class, center.getCustomerId());
		group = (CustomerBO) HibernateUtil.getSessionTL().get(CustomerBO.class,
				group.getCustomerId());
		AccountActionDateEntity installment = center.getCustomerAccount()
				.getAccountActionDate(Short.valueOf("1"));

		AccountFeesActionDetailEntity accountFeesAction = ((CustomerScheduleEntity) installment)
				.getAccountFeesAction(accountPeriodicFee.getAccountFeeId());
		assertEquals(5.0, accountFeesAction.getFeeAmount().getAmountDoubleValue(), DELTA);

		AccountActionDateEntity groupinstallment = group.getCustomerAccount()
				.getAccountActionDate(Short.valueOf("1"));

		AccountFeesActionDetailEntity groupaccountFeesAction = ((CustomerScheduleEntity) groupinstallment)
				.getAccountFeesAction(groupaccountPeriodicFee.getAccountFeeId());
		assertEquals(5.0, groupaccountFeesAction.getFeeAmount().getAmountDoubleValue(), DELTA);
	}
	
	public void testBatchJobException(){
		List<String> error = new ArrayList<String>();
		error.add("error1");
		error.add("error2");
		BatchJobException batchJobException = 
			new BatchJobException("error.invailddata",error);
		assertEquals("error.invailddata", batchJobException.getKey());
		assertEquals("error1,error2", batchJobException.getErrorMessage());
	}
	
	private java.sql.Date offSetDate(Date date , int noOfDays) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		calendar = new GregorianCalendar(year, month, day + noOfDays);
		return new java.sql.Date(calendar.getTimeInMillis());
	}

}
