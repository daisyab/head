package org.mifos.application.holiday.struts.action;

import java.util.List;

import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.checklist.business.AccountCheckListBO;
import org.mifos.application.checklist.business.CheckListBO;
import org.mifos.application.checklist.business.CustomerCheckListBO;
import org.mifos.application.checklist.persistence.CheckListPersistence;
import org.mifos.application.checklist.util.helpers.CheckListStatesView;
import org.mifos.application.checklist.util.helpers.CheckListType;
import org.mifos.application.checklist.util.resources.CheckListConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.util.resources.HolidayConstants;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestHolidayAction extends MifosMockStrutsTestCase {

	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
				.getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/holiday/struts-config.xml")
				.getPath());
		UserContext userContext = TestObjectFactory.getUserContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		//addRequestParameter("recordLoanOfficerId", "1");
		//addRequestParameter("recordOfficeId", "1");
		//ActivityContext ac = new ActivityContext((short) 0, userContext
		//		.getBranchId().shortValue(), userContext.getId().shortValue());
		//request.getSession(false).setAttribute("ActivityContext", ac);
		flowKey = createFlow(request, HolidayAction.class);
	}

	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}
 
	/*
	public void testLoadAllChecklist() throws Exception {
		CheckListBO checkList1 = TestObjectFactory.createAccountChecklist(
				ProductType.LOAN.getValue(),
				AccountState.LOANACC_ACTIVEINGOODSTANDING, (short) 1);
		CheckListBO checkList2 = TestObjectFactory.createAccountChecklist(
				ProductType.SAVINGS.getValue(),
				AccountState.SAVINGS_ACC_APPROVED, (short) 1);
		CheckListBO checkList3 = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), (short) 1);
		CheckListBO checkList4 = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.GROUP.getValue(), CustomerStatus.GROUP_ACTIVE
						.getValue(), (short) 1);

		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "loadAllChecklist");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.loadAllChecklist_success.toString());

		assertEquals(1, ((List) SessionUtils.getAttribute(
				CheckListConstants.LOAN_CHECKLIST, request)).size());
		assertEquals(1, ((List) SessionUtils.getAttribute(
				CheckListConstants.SAVINGS_CHECKLIST, request)).size());
		assertEquals(1, ((List) SessionUtils.getAttribute(
				CheckListConstants.CENTER_CHECKLIST, request)).size());
		assertEquals(1, ((List) SessionUtils.getAttribute(
				CheckListConstants.GROUP_CHECKLIST, request)).size());
		assertEquals(0, ((List) SessionUtils.getAttribute(
				CheckListConstants.CLIENT_CHECKLIST, request)).size());

		TestObjectFactory.cleanUp(checkList1);
		TestObjectFactory.cleanUp(checkList2);
		TestObjectFactory.cleanUp(checkList3);
		TestObjectFactory.cleanUp(checkList4);
	}

	public void testGetForCustomerChecklist() throws Exception {

		CheckListBO checkList = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), (short) 1);
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "get");
		addRequestParameter("checkListId", checkList.getChecklistId()
				.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.get_success.toString());

		assertNotNull(SessionUtils
				.getAttribute(Constants.BUSINESS_KEY, request));
		assertEquals(CheckListType.CUSTOMER_CHECKLIST.getValue(), SessionUtils
				.getAttribute(CheckListConstants.TYPE, request));
		assertNotNull(SessionUtils.getAttribute(
				CheckListConstants.CREATED_BY_NAME, request));

		TestObjectFactory.cleanUp(checkList);

	}

	public void testGetForAccountChecklist() throws Exception {
		CheckListBO checkList = TestObjectFactory.createAccountChecklist(
				ProductType.LOAN.getValue(),
				AccountState.LOANACC_ACTIVEINGOODSTANDING, (short) 1);

		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "get");
		addRequestParameter("checkListId", checkList.getChecklistId()
				.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.get_success.toString());

		assertNotNull(SessionUtils
				.getAttribute(Constants.BUSINESS_KEY, request));
		assertEquals(CheckListType.ACCOUNT_CHECKLIST.getValue(), SessionUtils
				.getAttribute(CheckListConstants.TYPE, request));
		assertNotNull(SessionUtils.getAttribute(
				CheckListConstants.CREATED_BY_NAME, request));

		TestObjectFactory.cleanUp(checkList);

	}
    */
	public void testLoad() throws Exception {
		setRequestPathInfo("/holidayAction");
		addRequestParameter("method", "load");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.load_success.toString());
		assertNotNull(SessionUtils.getAttribute(
				HolidayConstants.REPAYMENTRULETYPES, request)); //HOLIDAY_MASTERDATA
	}

	public void testGetHolidays() throws Exception {
		setRequestPathInfo("/holidayAction");
		addRequestParameter("method", "getHolidays");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.get_success.toString());
		assertNotNull(SessionUtils.getAttribute(HolidayConstants.HOLIDAYLIST1,
				request));
		assertNotNull(SessionUtils.getAttribute(HolidayConstants.HOLIDAYLIST2,
				request));
		assertEquals(0, ((List<HolidayBO>) SessionUtils.getAttribute(
				HolidayConstants.HOLIDAYLIST1, request)).size());
		assertEquals(0, ((List<HolidayBO>) SessionUtils.getAttribute(
				HolidayConstants.HOLIDAYLIST2, request)).size());
	}

	/*
	public void testPreviewNoChecklistName() {
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyInputForward();
	}

	public void testPreview() {
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "preview");
		addRequestParameter("masterTypeId", "3");
		addRequestParameter("isCustomer", "true");
		addRequestParameter("stateId", "13");
		addRequestParameter("checklistName", "checkListname");
		addRequestParameter("detailsList[0]", "newdetails");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.preview_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testPrevious() throws Exception {
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "previous");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.previous_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testCreate_customer() throws Exception {
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "create");
		addRequestParameter("masterTypeId", "1");
		addRequestParameter("masterTypeName", "kendra");
		addRequestParameter("isCustomer", "true");
		addRequestParameter("type", "1");
		addRequestParameter("stateId", "13");
		addRequestParameter("stateName", "active");
		addRequestParameter("checklistName", "new checklistName");
		addRequestParameter("detailsList[0]", "newdetails");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.create_success.toString());

		List<CustomerCheckListBO> customerCheckLists = new CheckListPersistence()
				.retreiveAllCustomerCheckLists();
		assertNotNull(customerCheckLists);
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		assertEquals(1, customerCheckLists.size());
		for (CustomerCheckListBO checkList : customerCheckLists) {
			assertEquals("new checklistName", checkList.getChecklistName());
			assertEquals("1", checkList.getCustomerLevel().getId().toString());
			assertEquals("13", checkList.getCustomerStatus().getId().toString());
			assertEquals("1", checkList.getChecklistStatus().toString());
			assertEquals(1, checkList.getChecklistDetails().size());
			TestObjectFactory.cleanUp(checkList);
		}
	}

	public void testCreate_product() throws Exception {
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "create");
		addRequestParameter("masterTypeId", "1");
		addRequestParameter("isCustomer", "false");
		addRequestParameter("stateId", "13");
		addRequestParameter("checklistName", "new checklistName");
		addRequestParameter("detailsList[0]", "newdetails");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.create_success.toString());

		List<AccountCheckListBO> accountCheckLists = new CheckListPersistence()
				.retreiveAllAccountCheckLists();
		assertNotNull(accountCheckLists);
		assertEquals(1, accountCheckLists.size());
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		for (AccountCheckListBO checkList : accountCheckLists) {
			assertEquals("new checklistName", checkList.getChecklistName());
			assertEquals("1", checkList.getProductTypeEntity()
					.getProductTypeID().toString());
			assertEquals("1", checkList.getChecklistStatus().toString());
			assertEquals(1, checkList.getChecklistDetails().size());
			TestObjectFactory.cleanUp(checkList);
		}
	}

	public void testCancelCreate() throws Exception {
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "cancelCreate");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward(ActionForwards.cancelCreate_success.toString());
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testManage_customerCenter() throws Exception {
		CheckListBO checkList = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), (short) 1);
		HibernateUtil.closeSession();
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "manage");
		addRequestParameter("checkListId", checkList.getChecklistId()
				.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		HibernateUtil.closeSession();
		checkList = (CheckListBO) TestObjectFactory.getObject(
				CustomerCheckListBO.class, checkList.getChecklistId());
		TestObjectFactory.cleanUp(checkList);
	}

	public void testManage_customerGroup() throws Exception {
		CheckListBO checkList = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.GROUP.getValue(), CustomerStatus.GROUP_ACTIVE
						.getValue(), (short) 1);
		HibernateUtil.closeSession();
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "manage");
		addRequestParameter("checkListId", checkList.getChecklistId()
				.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		HibernateUtil.closeSession();
		checkList = (CheckListBO) TestObjectFactory.getObject(
				CustomerCheckListBO.class, checkList.getChecklistId());
		TestObjectFactory.cleanUp(checkList);
	}

	public void testManage_customerClient() throws Exception {
		CheckListBO checkList = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.CLIENT.getValue(), CustomerStatus.CLIENT_ACTIVE
						.getValue(), (short) 1);
		HibernateUtil.closeSession();
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "manage");
		addRequestParameter("checkListId", checkList.getChecklistId()
				.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		HibernateUtil.closeSession();
		checkList = (CheckListBO) TestObjectFactory.getObject(
				CustomerCheckListBO.class, checkList.getChecklistId());
		TestObjectFactory.cleanUp(checkList);
	}

	public void testManage_accountLoan() throws Exception {
		CheckListBO checkList = TestObjectFactory.createAccountChecklist(
				ProductType.LOAN.getValue(),
				AccountState.LOANACC_ACTIVEINGOODSTANDING, (short) 1);
		HibernateUtil.closeSession();
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "manage");
		addRequestParameter("checkListId", checkList.getChecklistId()
				.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		HibernateUtil.closeSession();
		checkList = (CheckListBO) TestObjectFactory.getObject(
				AccountCheckListBO.class, checkList.getChecklistId());
		TestObjectFactory.cleanUp(checkList);
	}

	public void testManage_accountSaving() throws Exception {
		CheckListBO checkList = TestObjectFactory.createAccountChecklist(
				ProductType.SAVINGS.getValue(),
				AccountState.SAVINGS_ACC_APPROVED, (short) 1);
		HibernateUtil.closeSession();
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "manage");
		addRequestParameter("checkListId", checkList.getChecklistId()
				.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		HibernateUtil.closeSession();
		checkList = (CheckListBO) TestObjectFactory.getObject(
				AccountCheckListBO.class, checkList.getChecklistId());
		TestObjectFactory.cleanUp(checkList);
	}

	public void testGetEditStates() throws Exception {
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "getEditStates");
		addRequestParameter("masterTypeId", "1");
		addRequestParameter("isCustomer", "false");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		assertNotNull(SessionUtils.getAttribute(CheckListConstants.STATES,
				request));
		assertEquals(6, ((List<CheckListStatesView>) SessionUtils
				.getAttribute(CheckListConstants.STATES, request)).size());
	}

	public void testManagePreviewValidate() {
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "managePreview");
		addRequestParameter("masterTypeId", "3");
		addRequestParameter("isCustomer", "true");
		addRequestParameter("stateId", "13");
		addRequestParameter("detailsList[0]", "newdetails");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyActionErrors(new String[] { CheckListConstants.MANDATORY,
				CheckListConstants.MANDATORY });
		verifyInputForward();
	}

	public void testManagePreview() {
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "managePreview");
		addRequestParameter("masterTypeId", "3");
		addRequestParameter("isCustomer", "true");
		addRequestParameter("stateId", "13");
		addRequestParameter("checklistName", "checkListname");
		addRequestParameter("checklistStatus", CheckListConstants.STATUS_ACTIVE
				.toString());
		addRequestParameter("detailsList[0]", "newdetails");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.managepreview_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testManagePrevious() {
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "managePrevious");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.manageprevious_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testUpdate_customer() throws Exception{
		CheckListBO checkList = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), (short) 1);
		HibernateUtil.closeSession();
		checkList = (CheckListBO) TestObjectFactory.getObject(
				CustomerCheckListBO.class, checkList.getChecklistId());
		
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "update");
		addRequestParameter("checklistName", checkList.getChecklistName());
		addRequestParameter("isCustomer", "true");
		addRequestParameter("detailsList[0]", "newdetails");
		addRequestParameter("masterTypeId", ((CustomerCheckListBO)checkList).getCustomerLevel()
				.getId().toString());
		addRequestParameter("stateId", ((CustomerCheckListBO)checkList).getCustomerStatus()
				.getId().toString());
		addRequestParameter("checklistStatus", CheckListConstants.STATUS_INACTIVE.toString());
		
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,checkList,request);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.update_success.toString());
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		HibernateUtil.closeSession();
		checkList = (CheckListBO) TestObjectFactory.getObject(
				CustomerCheckListBO.class, checkList.getChecklistId());
		assertEquals(1,checkList.getChecklistDetails().size());
		TestObjectFactory.cleanUp(checkList);
	}
	
	public void testUpdate_product() throws Exception{
		CheckListBO checkList = TestObjectFactory.createAccountChecklist(
				ProductType.LOAN.getValue(),
				AccountState.LOANACC_ACTIVEINGOODSTANDING, (short) 1);
		HibernateUtil.closeSession();
		checkList = (CheckListBO) TestObjectFactory.getObject(
				AccountCheckListBO.class, checkList.getChecklistId());
		
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "update");
		addRequestParameter("checklistName", checkList.getChecklistName());
		addRequestParameter("isCustomer", "false");
		addRequestParameter("detailsList[0]", "newdetails");
		addRequestParameter("masterTypeId", ((AccountCheckListBO)checkList).getProductTypeEntity()
				.getProductTypeID().toString());
		addRequestParameter("stateId", ((AccountCheckListBO)checkList).getAccountStateEntity()
				.getId().toString());
		addRequestParameter("checklistStatus", CheckListConstants.STATUS_INACTIVE.toString());
		
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,checkList,request);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.update_success.toString());
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		HibernateUtil.closeSession();
		checkList = (CheckListBO) TestObjectFactory.getObject(
				AccountCheckListBO.class, checkList.getChecklistId());
		assertEquals(1,checkList.getChecklistDetails().size());
		TestObjectFactory.cleanUp(checkList);
	}
	
	public void testCancelManage() {
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "cancelManage");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.cancelEdit_success.toString());
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}
	*/
}
