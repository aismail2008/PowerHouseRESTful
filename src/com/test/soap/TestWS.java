package com.test.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

//In web.xml add the following lines :
//
//
//<servlet>
//		<servlet-name>MissionsWorkFlowWSHttpPort</servlet-name>
//		<servlet-class>com.code.integration.webservices.workflow.hcm.MissionsWorkFlowWS</servlet-class>
//		<load-on-startup>1</load-on-startup>
//	</servlet>
//	<servlet-mapping>
//		<servlet-name>MissionsWorkFlowWSHttpPort</servlet-name>
//		<url-pattern>/MissionsWorkFlowWS</url-pattern>
//	

@WebService(targetNamespace = "http://integration.code.com/missions", portName = "MissionsWorkFlowWSHttpPort")
public class TestWS {

	@WebMethod(operationName = "getWfMissionAndWfMissionDetails")
	@WebResult(name = "wfMissionAndWFMissionDetailsResponse")
	public WSWFMissionResponse getWFMissionAndWFMissionDetails(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId, @WebParam(name = "taskId") long taskId) {

		// WSWFMissionResponse response = new WSWFMissionResponse();
		// if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
		// return response;
		//
		// try {
		//
		// WFTask wfTask = BaseWorkFlow.getWFTaskById(taskId);
		// if (employeeId != wfTask.getAssigneeId())
		// throw new BusinessException("error_general");
		//
		// WFMissionData wfMission = MissionsWorkFlow.getWFMissionByInstanceId(wfTask.getInstanceId());
		// List<WFMissionDetailData> wfMissionDetailDataList = MissionsWorkFlow.getWFMissionDetailsByInstanceId(wfTask.getInstanceId());
		//
		// if (wfMission == null)
		// throw new BusinessException("error_general");
		//
		// response.setWfMission(wfMission);
		// response.setWfMissionDetails(wfMissionDetailDataList);
		//
		// response.setMessage(BaseService.getMessage("notify_successOperation"));
		// } catch (Exception e) {
		// response.setStatus(WSResponseStatusEnum.FAILED.getCode());
		// response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
		// if (!(e instanceof BusinessException))
		// e.printStackTrace();
		// }
		//
		// return response;
		return null;
	}
}