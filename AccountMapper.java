package vip.xiaonuo.sys.modular.monitorProbe.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import vip.xiaonuo.sys.modular.monitorProbe.model.AccountLabel;
import vip.xiaonuo.sys.modular.monitorProbe.model.Employee;
import vip.xiaonuo.sys.modular.monitorProbe.model.JobTypeAndAccountCount;
import vip.xiaonuo.sys.modular.monitorProbe.model.Test;
import vip.xiaonuo.sys.modular.monitorProbe.model.input.AccountListParam;
import vip.xiaonuo.sys.modular.monitorProbe.model.input.AccountUpdateParam;
import vip.xiaonuo.sys.modular.monitorProbe.model.input.AliveTimeParam;
import vip.xiaonuo.sys.modular.monitorProbe.model.input.AttendanceGroupAccountListParam;
import vip.xiaonuo.sys.modular.monitorProbe.model.input.CollectDataDetailParam;
import vip.xiaonuo.sys.modular.monitorProbe.model.input.MonitorAccountParam;
import vip.xiaonuo.sys.modular.monitorProbe.model.input.QueryAccountByScheduleIdParam;
import vip.xiaonuo.sys.modular.monitorProbe.model.output.Account;
import vip.xiaonuo.sys.modular.monitorProbe.model.output.AccountDetail;
import vip.xiaonuo.sys.modular.monitorProbe.model.output.AccountOutput;
import vip.xiaonuo.sys.modular.monitorProbe.model.output.CollectDataDetail;
import vip.xiaonuo.sys.modular.monitorProbe.model.output.KeyboardAccountInfo;
import vip.xiaonuo.sys.modular.monitorProbe.model.output.MonitorAccount;
import vip.xiaonuo.sys.modular.monitorProbe.model.output.MonitorAccountDto;
import vip.xiaonuo.sys.modular.monitorProbe.model.output.PersonalAccountDetail;
import vip.xiaonuo.sys.modular.monitorProbe.model.output.RulePersonOutput;

/**
 * @author by libi
 * @date 2022/10/25
 */
@Mapper
public interface AccountMapper {
    int test(Test test);

    List<Account> getAllAccount(@Param("userName") String userName, @Param("jobType") String jobType, @Param("projectId") Long projectId, @Param("version") String version, @Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    int getAllAccountCount(@Param("userName") String userName, @Param("jobType") String jobType, @Param("projectId") Long projectId, @Param("version") String version);

    Account getAccountById(@Param("id") int id);

    List<Account> getAccountList(AccountListParam accountListParam);

    int getAccountListCount(AccountListParam accountListParam);

    String getBootTimeByMacAddress(@Param("macAddress") String macAddress);

    List<MonitorAccount> getAllMonitorAccount(@Param("dataScopeList") List<Long> dataScopeList);

    List<AccountDetail> getMonitorAccountDetail(@Param("id") int id, @Param("timeType") String timeType, @Param("startTime") String startTime, @Param("stopTime") String stopTime, @Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);

    int getMonitorAccountDetailCount(@Param("id") int id, @Param("timeType") String timeType, @Param("startTime") String startTime, @Param("stopTime") String stopTime);

    int updateAccount(AccountUpdateParam accountUpdateParam);

    /**
     * 根据条件获取信息
     * @param collectDataDetailParam 查询条件
     * @return 查询结果
     */
    CollectDataDetail getCollectDateDetail(CollectDataDetailParam collectDataDetailParam);

    PersonalAccountDetail getPersonalAccountDetail(@Param("id") int id);

    List<Account> getAllAccountNoPage(@Param("isUsing") int isUsing);

    AccountLabel getAccountByMacAddress(@Param("macAddress") String macAddress);

    int updateAccountLabelInfo(@Param("macAddress") String macAddress, @Param("accountLabel") String accountLabel);

    int insertAccountLabelInfo(AccountLabel accountLabel);

    List<JobTypeAndAccountCount> getJobListAndAccountCount();

    int getCountByGroupId(@Param("groupId") Long groupId);

    int getCountByCondition(@Param("param") AttendanceGroupAccountListParam param);

    List<Account> getAccountListByCondition(@Param("param") AttendanceGroupAccountListParam param, @Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    int updateGroupInfoById(@Param("id") Integer id);

    int updateGroupInfoBatch(@Param("ids") int[] ids, @Param("groupId") Long groupId);

    int updateAllGroupInfo(@Param("oldGroupId") Long groupId,@Param("targetGroupId") Long targetGroupId, @Param("ids") int[] ids);

    int updateScheduleRuleInfoBatch(@Param("ids") int[] ids, @Param("ruleId") Long ruleId);

    int updateAllScheduleRuleUser(@Param("oldRuleId") Long ruleId, @Param("targetRuleId")Long targetRuleId, @Param("ids") int[] ids);

    int updateGroupInfo(@Param("groupId") Long groupId);

    int addAccount(Account account);

    Long getGroupIdByMacAddress(@Param("macAddress") String macAddress);

    List<String> getAllAccountMacAddress(@Param("userName") String userName, @Param("jobType") String jobType, @Param("projectId") Long projectId, @Param("version") String version, @Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    List<AccountOutput> getAllAccountOutput(AliveTimeParam aliveTimeParam);

    Account getAccountInfoByMacAddress(@Param("macAddress") String macAddress);

    void updateAccountTimeType(@Param("batchUpdateList") List<AccountDetail> batchUpdateList);

    List<RulePersonOutput> queryRuleAccountOutput(@Param("id") Long id,@Param("date") String executeDate);

    Employee getEmployeeByMacAddress(@Param("macAddress")String macAddress);

    KeyboardAccountInfo selectByIdForKeyboard(@Param("id")int id);

    List<Account> getAccountListByScheduleRuleId(@Param("param")QueryAccountByScheduleIdParam param, @Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    int getAccountListCountByScheduleRuleId(@Param("param")QueryAccountByScheduleIdParam queryAccountByScheduleIdParam);

    int updateAccountScheduleRuleId(@Param("id") int id, @Param("scheduleId") Long scheduleId);

    int resetScheduleRuleId(@Param("ruleId") Long ruleId, @Param("scheduleId") Long scheduleId);

    List<Employee> getEmployeeById(@Param("ids")List<Integer> ids);

    /**
     * 查询监控账号-条数
     * @param monitorAccountParam 查询条件
     * @return 查询结果
     */
    Integer getMonitorAccountCount(MonitorAccountParam monitorAccountParam);

    /**
     * 查询监控账号
     * @param monitorAccountParam 查询条件
     * @return 查询结果
     */
    List<MonitorAccountDto> getMonitorAccountList(MonitorAccountParam monitorAccountParam);

    Integer getCountByScheduleRule(Long scheduleRuleId);
}
