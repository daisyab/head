package org.mifos.application.productdefinition.business.service;

import java.sql.Date;
import java.util.List;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanPrdBusinessServiceTest extends MifosTestCase {

	public LoanPrdBusinessServiceTest() throws SystemException, ApplicationException {
        super();
    }

    private LoanOfferingBO loanOffering;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(loanOffering);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetBusinessObject() throws ServiceException {
		assertNull(new LoanPrdBusinessService().getBusinessObject(null));
	}

	public void testGetActiveLoanProductCategoriesForInvalidConnection() {
		TestObjectFactory.simulateInvalidConnection();
		try {
			assertEquals(1, new LoanPrdBusinessService()
					.getActiveLoanProductCategories().size());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
	}

	public void testGetActiveLoanProductCategories() throws ServiceException {
		assertEquals(1, new LoanPrdBusinessService()
				.getActiveLoanProductCategories().size());
	}

	public void testGetLoanApplicableCustomerTypesForInvalidConnection() {
		TestObjectFactory.simulateInvalidConnection();
		try {
			new LoanPrdBusinessService()
					.getLoanApplicableCustomerTypes((short) 1);
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
	}

	public void testGetLoanApplicableCustomerTypes() throws ServiceException {
		assertEquals(2, new LoanPrdBusinessService()
				.getLoanApplicableCustomerTypes((short) 1).size());
	}

	public void testGetLoanOffering() throws ServiceException {
		loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
		HibernateUtil.closeSession();

		loanOffering = new LoanPrdBusinessService()
				.getLoanOffering(loanOffering.getPrdOfferingId());
		assertNotNull(loanOffering);
		assertEquals("Loan Offering", loanOffering.getPrdOfferingName());
		assertEquals("Loan", loanOffering.getPrdOfferingShortName());
	}

	public void testGetLoanOfferingForInvalidConnection()
			throws ServiceException {
		loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
		HibernateUtil.closeSession();

		TestObjectFactory.simulateInvalidConnection();
		try {
			new LoanPrdBusinessService().getLoanOffering(loanOffering
					.getPrdOfferingId());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
	}

	public void testGetApplicablePrdStatus() throws ServiceException {
		List<PrdStatusEntity> prdStatusList = new LoanPrdBusinessService()
				.getApplicablePrdStatus((short) 1);
		HibernateUtil.closeSession();
		assertEquals(2, prdStatusList.size());
		for (PrdStatusEntity prdStatus : prdStatusList) {
			if (prdStatus.getPrdState().equals("1"))
				assertEquals("Active", prdStatus.getPrdState().getName());
			if (prdStatus.getPrdState().equals("2"))
				assertEquals("InActive", prdStatus.getPrdState().getName());
		}
	}

	public void testGetApplicablePrdStatusForInvalidConnection() {
		TestObjectFactory.simulateInvalidConnection();
		try {
			new LoanPrdBusinessService().getApplicablePrdStatus((short) 1);
			fail();
		} catch (ServiceException e) {
		}
	}

	public void testGetLoanOfferingWithLocaleId() throws ServiceException {
		loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
		HibernateUtil.closeSession();

		short localeId = 1;
		loanOffering = new LoanPrdBusinessService().getLoanOffering(
				loanOffering.getPrdOfferingId(), localeId);
		assertNotNull(loanOffering);
		assertEquals("Loan Offering", loanOffering.getPrdOfferingName());
		assertEquals("Loan", loanOffering.getPrdOfferingShortName());

		assertEquals("Other", loanOffering.getPrdCategory()
				.getProductCategoryName());
		assertEquals(ApplicableTo.GROUPS, 
				loanOffering.getPrdApplicableMasterEnum());
		assertEquals("Active", loanOffering.getPrdStatus().getPrdState()
				.getName());
		assertEquals("Grace on all repayments", loanOffering
				.getGracePeriodType().getName());
		assertEquals("Flat", loanOffering.getInterestTypes().getName());
	}

	public void testGetLoanOfferingWithLocaleIdForInvalidConnection() {
		loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
		HibernateUtil.closeSession();

		TestObjectFactory.simulateInvalidConnection();
		try {
			new LoanPrdBusinessService().getLoanOffering(loanOffering
					.getPrdOfferingId(), (short) 1);
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
	}

	public void testGetAllLoanOfferings() throws ServiceException {
		loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
		LoanOfferingBO loanOffering1 = createLoanOfferingBO("Loan Offering1",
				"Loa1");
		HibernateUtil.closeSession();

		List<LoanOfferingBO> loanOfferings = new LoanPrdBusinessService()
				.getAllLoanOfferings((short) 1);
		assertNotNull(loanOfferings);
		assertEquals(2, loanOfferings.size());
		for (LoanOfferingBO loanOfferingBO : loanOfferings) {
			assertNotNull(loanOfferingBO.getPrdOfferingName());
			assertNotNull(loanOfferingBO.getPrdOfferingId());
			assertNotNull(loanOfferingBO.getPrdStatus().getPrdState().getName());
		}
		HibernateUtil.closeSession();
		TestObjectFactory.removeObject(loanOffering1);
	}

	public void testGetAllLoanOfferingsForInvalidConnection() {
		loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
		HibernateUtil.closeSession();

		TestObjectFactory.simulateInvalidConnection();
		try {
			new LoanPrdBusinessService().getAllLoanOfferings((short) 1);
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
	}

	public void testRetrieveLatenessForPrd() throws Exception{
		try {
			Short latenessDays = new LoanPrdBusinessService().retrieveLatenessForPrd();
			assertEquals(latenessDays,Short.valueOf("10"));
		} catch (ServiceException e) {
			assertTrue(false);
		}
		HibernateUtil.closeSession();
		
	}
	
	public void testRetrieveLatenessForPrdForInvalidConnection() throws Exception{
		TestObjectFactory.simulateInvalidConnection();
		try {
			new LoanPrdBusinessService().retrieveLatenessForPrd();
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
		
	}
	
	private LoanOfferingBO createLoanOfferingBO(String prdOfferingName,
			String shortName) {
		Date startDate = new Date(System.currentTimeMillis());

		MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		return TestObjectFactory.createLoanOffering(prdOfferingName, shortName,
				ApplicableTo.GROUPS, startDate, 
				PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, 
				InterestType.FLAT, frequency);
	}
}
