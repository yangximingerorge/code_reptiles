package vip.xiaonuo.sys.modular.monitorProbe.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.core.context.login.LoginContextHolder;
import vip.xiaonuo.core.pojo.response.ErrorResponseData;
import vip.xiaonuo.core.pojo.response.ResponseData;
import vip.xiaonuo.core.pojo.response.SuccessResponseData;
import vip.xiaonuo.sys.modular.dict.service.SysDictDataService;
import vip.xiaonuo.sys.modular.monitorProbe.error.ErrorMessage;
import vip.xiaonuo.sys.modular.monitorProbe.mapper.AccountMapper;
import vip.xiaonuo.sys.modular.monitorProbe.mapper.AttendanceGroupMapper;
import vip.xiaonuo.sys.modular.monitorProbe.mapper.CalendarBasicInfoMapper;
import vip.xiaonuo.sys.modular.monitorProbe.mapper.CalendarDetailInfoMapper;
import vip.xiaonuo.sys.modular.monitorProbe.mapper.EmployeeMapper;
import vip.xiaonuo.sys.modular.monitorProbe.model.AttendanceGroup;
import vip.xiaonuo.sys.modular.monitorProbe.model.CalendarBasic;
import vip.xiaonuo.sys.modular.monitorProbe.model.CalendarDetail;
import vip.xiaonuo.sys.modular.monitorProbe.model.input.AttendanceGroupAccountListParam;
import vip.xiaonuo.sys.modular.monitorProbe.model.input.AttendanceGroupAddParam;
import vip.xiaonuo.sys.modular.monitorProbe.model.input.AttendanceGroupQuery;
import vip.xiaonuo.sys.modular.monitorProbe.model.input.AttendanceGroupUpdateParam;
import vip.xiaonuo.sys.modular.monitorProbe.model.input.DelAttendanceGroupParam;
import vip.xiaonuo.sys.modular.monitorProbe.model.input.GroupAddUserParam;
import vip.xiaonuo.sys.modular.monitorProbe.model.output.Account;
import vip.xiaonuo.sys.modular.monitorProbe.model.output.AttendanceCalDto;
import vip.xiaonuo.sys.modular.monitorProbe.model.output.CalLiteInfo;
import vip.xiaonuo.sys.modular.monitorProbe.service.AttendanceGroupService;
import vip.xiaonuo.sys.modular.monitorProbe.utils.Constant;
import vip.xiaonuo.sys.modular.monitorProbe.utils.Util;

/**
 * className: AttendanceGroupServiceImpl
 * description: 考勤组服务实现
 * author: zhouyongchao
 * date: 2023/1/4 14:40
 * version: 1.0
 */
@Slf4j
@Service
public class AttendanceGroupServiceImpl extends ServiceImpl<AttendanceGroupMapper, AttendanceGroup> implements AttendanceGroupService {

    @Resource
    private AccountMapper accountMapper;

    @Autowired
    private CalendarBasicInfoMapper calendarBasicInfoMapper;

    @Resource
    private SysDictDataService sysDictDataService;

    @Autowired
    private AttendanceGroupMapper attendanceGroupMapper;

    @Autowired
    private CalendarDetailInfoMapper calendarDetailInfoMapper;

    @Resource
    private EmployeeMapper employeeMapper;

    @Override
    public Map<String,Object> getAllAttendanceGroupList(String groupName, int pageNo, int pageSize) {
        LambdaQueryWrapper<AttendanceGroup> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(groupName)) {
            queryWrapper.like(AttendanceGroup::getGroupName, groupName);
        }
        Page<AttendanceGroup> page = new Page<>(pageNo, pageSize);
        Page<AttendanceGroup> attendanceGroupPage = this.baseMapper.selectPage(page, queryWrapper);
        Map<String, Object> result = new HashMap<>();
        result.put("total", Integer.parseInt(String.valueOf(attendanceGroupPage.getTotal())));
        List<Map<String, Object>> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(attendanceGroupPage.getRecords())) {
            for (AttendanceGroup attendanceGroup : attendanceGroupPage.getRecords()) {
                Map<String, Object> attendanceGroupInfo = new HashMap<>();
                attendanceGroupInfo.put("id", attendanceGroup.getId());
                attendanceGroupInfo.put("groupName", attendanceGroup.getGroupName());
                attendanceGroupInfo.put("goWorkTime", attendanceGroup.getGoWorkTime());
                attendanceGroupInfo.put("afterWorkTime", attendanceGroup.getAfterWorkTime());
                attendanceGroupInfo.put("accountCount", accountMapper.getCountByGroupId(attendanceGroup.getId()));
                list.add(attendanceGroupInfo);
            }
        }
        result.put("list", list);
        result.put("pageNo", pageNo);
        result.put("pageSize", pageSize);
        return result;
    }

    @Override
    public AttendanceGroup getAttendanceGroupById(Long id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public Map<String, Object> getAccountListByGroupId(AttendanceGroupAccountListParam param) {
        Map<String, Object> result = new HashMap<>();
        result.put("total", accountMapper.getCountByCondition(param));
        int startIndex = Util.getQueryStartIndex(param.getPageNo(), param.getPageSize());
        List<Account> accounts = accountMapper.getAccountListByCondition(param, startIndex, param.getPageSize());
        result.put("list", accounts);
        result.put("pageNo", param.getPageNo());
        result.put("pageSize", param.getPageSize());
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseData addAttendanceGroup(AttendanceGroupAddParam attendanceGroupAddParam) {
        // 新增考勤组信息
        AttendanceGroup attendanceGroup = new AttendanceGroup();
        attendanceGroup.setGroupName(attendanceGroupAddParam.getGroupName());
        attendanceGroup.setGoWorkTime(attendanceGroupAddParam.getGoWorkTime());
        attendanceGroup.setAfterWorkTime(attendanceGroupAddParam.getAfterWorkTime());
        attendanceGroup.setRestBeginTime(attendanceGroupAddParam.getRestBeginTime());
        attendanceGroup.setRestEndTime(attendanceGroupAddParam.getRestEndTime());
        attendanceGroup.setCalendarId(attendanceGroupAddParam.getCalendarId());
        attendanceGroup.setCreateName(LoginContextHolder.me().getSysLoginUserAccount());
        attendanceGroup.setCreateTime(new Date());
        boolean save = this.save(attendanceGroup);
        if (!save) {
            return new ErrorResponseData(ErrorMessage.INSERT_ATTENDANCE_GROUP_ERROR_CODE, ErrorMessage.INSERT_ATTENDANCE_GROUP_ERROR_MSG);
        }
        // 修改用户考勤组信息
        if (attendanceGroupAddParam.getIdArray() != null && attendanceGroupAddParam.getIdArray().length != 0) {
            accountMapper.updateGroupInfoBatch(attendanceGroupAddParam.getIdArray(), attendanceGroup.getId());
        }
        return new SuccessResponseData();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseData updateAttendanceGroup(AttendanceGroupUpdateParam attendanceGroupUpdateParam) {
        // 修改考勤组信息
        LambdaUpdateWrapper<AttendanceGroup> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(AttendanceGroup::getGroupName, attendanceGroupUpdateParam.getGroupName())
                .set(AttendanceGroup::getGoWorkTime, attendanceGroupUpdateParam.getGoWorkTime())
                .set(AttendanceGroup::getAfterWorkTime, attendanceGroupUpdateParam.getAfterWorkTime())
                .set(AttendanceGroup::getRestBeginTime, attendanceGroupUpdateParam.getRestBeginTime())
                .set(AttendanceGroup::getRestEndTime, attendanceGroupUpdateParam.getRestEndTime())
                .set(AttendanceGroup::getCalendarId, attendanceGroupUpdateParam.getCalendarId())
                .set(AttendanceGroup::getCreateName, LoginContextHolder.me().getSysLoginUserAccount())
                .set(AttendanceGroup::getCreateTime, new Date())
                .eq(AttendanceGroup::getId, attendanceGroupUpdateParam.getId());
        boolean update = this.update(updateWrapper);
        if (!update) {
            return new ErrorResponseData(ErrorMessage.UPDATE_ATTENDANCE_GROUP_ERROR_CODE, ErrorMessage.UPDATE_ATTENDANCE_GROUP_ERROR_MSG);
        }
        /*// 修改的功能是先删再修改
        int delete = accountMapper.updateGroupInfo(attendanceGroupUpdateParam.getId());
        if (delete <= 0) {
            return new ErrorResponseData(ErrorMessage.UPDATE_ATTENDANCE_GROUP_ERROR_CODE, ErrorMessage.UPDATE_ATTENDANCE_GROUP_ERROR_MSG);
        }
        // 修改用户考勤组信息
        if (attendanceGroupUpdateParam.getIdArray() != null && attendanceGroupUpdateParam.getIdArray().length != 0) {
            accountMapper.updateGroupInfoBatch(attendanceGroupUpdateParam.getIdArray(), attendanceGroupUpdateParam.getId());
        }*/
        return new SuccessResponseData();
    }

    @Override
    public ResponseData deleteGroupInfoById(Integer id) {
        int update = accountMapper.updateGroupInfoById(id);
        if (update > 0) {
            return new SuccessResponseData();
        } else {
            return new ErrorResponseData(ErrorMessage.UPDATE_ACCOUNT_GROUP_ERROR_CODE, ErrorMessage.UPDATE_ACCOUNT_GROUP_ERROR_MSG);
        }
    }

    @Override
    public Integer countByCalendarId(Long id) {
        LambdaQueryWrapper<AttendanceGroup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AttendanceGroup::getCalendarId, id);
        return this.count(queryWrapper);
    }

    @Override
    public Map<String, Object> getAccountInfoByProjectId(Long projectId, String userName, int pageNo, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        int total = accountMapper.getAllAccountCount(userName, null, projectId, null);
        int startIndex = Util.getQueryStartIndex(pageNo, pageSize);
        List<Account> accounts = accountMapper.getAllAccount(userName, null, projectId, null, startIndex, pageSize);

        List<Map<String, Object>> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(accounts)) {
            // 查询岗位列表信息，用于映射枚举的中文含义
            Map<String, String> jobTypeList = sysDictDataService.getDictDataListByDictTypeCode(Constant.JOB_TYPE);
             for (Account account : accounts) {
                Map<String, Object> accountInfo = new HashMap<>();
                accountInfo.put("id", account.getId());
                accountInfo.put("userName", account.getUserName());
                accountInfo.put("projectName", account.getProjectName());
                accountInfo.put("companyName",  account.getCompanyName());
                accountInfo.put("macAddress", account.getMacAddress());
                accountInfo.put("jobType", MapUtils.getString(jobTypeList, account.getJobType()));
                accountInfo.put("groupId", account.getGroupId());
                accountInfo.put("screenShotOpen", account.getScreenshotOpen());
                accountInfo.put("scheduleRuleId", account.getScheduleRuleId());
                list.add(accountInfo);
            }
        }
        result.put("list", list);
        result.put("total", total);
        result.put("pageNo", pageNo);
        result.put("pageSize", pageSize);
        return result;
    }

    @Override
    public List<CalendarBasic> getCalendarList() {
        LambdaQueryWrapper<CalendarBasic> queryWrapper = new LambdaQueryWrapper<>();
        return calendarBasicInfoMapper.selectList(queryWrapper);
    }

    @Override
    public Map<Integer, Map<LocalDate, CalLiteInfo>> queryCalendar(String startDate, String endDate, Long... groupIds) {
        return this.queryCalendar(startDate, endDate, new HashSet<>(Arrays.asList(groupIds)));


    }

    @Override
    public Map<Integer, Map<LocalDate, CalLiteInfo>> queryCalendar(String startDate, String endDate, Set<Long> groupIds) {
        //根据考勤组ID，获取日历ID和是否通用信息
        AttendanceGroupQuery groupQuery = new AttendanceGroupQuery();
        groupQuery.setGroupIdList(new ArrayList<>(groupIds));
        List<AttendanceCalDto> groupCalList = attendanceGroupMapper.listGroupCalInfo(groupQuery);

        //获取指定日期内的通用日历信息
        LambdaQueryWrapper<CalendarBasic> basicCalWrapper = new LambdaQueryWrapper<>();
        basicCalWrapper.eq(CalendarBasic::getIsCommon,"0")
                .select(CalendarBasic::getId)
                .last("limit 1");
        CalendarBasic basicConfig = calendarBasicInfoMapper.selectOne(basicCalWrapper);
        LambdaQueryWrapper<CalendarDetail> commonCalWrapper = new LambdaQueryWrapper<>();
        commonCalWrapper.between(CalendarDetail::getCalendarDate, startDate, endDate)
                .eq(CalendarDetail::getBasicId, basicConfig.getId())
                .select(CalendarDetail::getBasicId, CalendarDetail::getCalendarDate, CalendarDetail::getIsWork);
        List<CalendarDetail> commonCalList = calendarDetailInfoMapper.selectList(commonCalWrapper);
        Map<LocalDate, CalendarDetail> commonCalMap = commonCalList.stream()
                .collect(Collectors.toMap(
                        dto -> LocalDate.parse(dto.getCalendarDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        Function.identity()
        ));

        //根据日历ID，获取指定日期的日历信息
        Set<Integer> calSet = groupCalList.stream().map(AttendanceCalDto::getCalendarId).collect(Collectors.toSet());

        List<CalendarDetail> calGroupList = new ArrayList();

        //如果有考勤组信息，则获取考勤组对应的日历信息
        if(ObjectUtil.isNotEmpty(calSet)){
            LambdaQueryWrapper<CalendarDetail> calWrapper = new LambdaQueryWrapper<>();
            calWrapper.between(CalendarDetail::getCalendarDate, startDate, endDate)
                    .in(CalendarDetail::getBasicId, calSet)
                    .select(CalendarDetail::getBasicId, CalendarDetail::getCalendarDate, CalendarDetail::getIsWork);
            calGroupList = calendarDetailInfoMapper.selectList(calWrapper);
        }

        Map<Long, List<CalendarDetail>> calGroupMap = calGroupList.stream().collect(Collectors.groupingBy(CalendarDetail::getBasicId));

        //根据考勤组、考勤日历、通用日历设置map
        Map<Integer, Map<LocalDate, CalLiteInfo>> groupCalMap = new HashMap<>();


        for (AttendanceCalDto attendanceCalDto : groupCalList) {
            LocalDate begin = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            //获取本考勤组的日历信息
            List<CalendarDetail> calList = new ArrayList<>();
            if(calGroupMap.containsKey(Long.valueOf(String.valueOf(attendanceCalDto.getCalendarId())))){
                calList = calGroupMap.get(Long.valueOf(String.valueOf(attendanceCalDto.getCalendarId())));
            }
            Map<LocalDate, CalendarDetail> calMap = calList.stream().collect(Collectors.toMap(
                    dto -> LocalDate.parse(dto.getCalendarDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    Function.identity()
            ));

            Map<LocalDate, CalLiteInfo> calInfoMap = new HashMap<>();
            for(;begin.compareTo(end) <= 0; begin = begin.plusDays(1L)){
                CalLiteInfo liteInfo = new CalLiteInfo();
                liteInfo.setDate(begin);
                //如果当前考勤组的日历，没有信息，则使用通用日历
                if(!calMap.containsKey(begin)){
                    liteInfo.setIsWork(Integer.parseInt(commonCalMap.get(begin).getIsWork()));
                }else{
                    liteInfo.setIsWork(Integer.parseInt(calMap.get(begin).getIsWork()));
                }
                calInfoMap.put(begin, liteInfo);
            }
            groupCalMap.put(attendanceCalDto.getGroupId(), calInfoMap);
        }
        return groupCalMap;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseData addEmployee(GroupAddUserParam addUserParam) {
        //TODO当前账户操作员工的权限检查
        //指定员工的考勤组更新
        if(ObjectUtil.isNotEmpty(addUserParam.getEmployeeIds())){
            //原有成员全部清除
            //accountMapper.updateAllGroupInfo(addUserParam.getGroupId(), 0L, null);

            //选择的人员更新为当前考勤组
            accountMapper.updateAllGroupInfo(0L, addUserParam.getGroupId(), addUserParam.getEmployeeIds());
        }

        if(ObjectUtil.isNotEmpty(addUserParam.getDeleteEmployeeIds())){
            accountMapper.updateAllGroupInfo(addUserParam.getGroupId(), 0L, addUserParam.getDeleteEmployeeIds());
        }


        //由于可能存在重复提交等情况，update值可能为0，此处暂时不判断
        return new SuccessResponseData();
    }


    /**
     * 考情组删除
     * @param delAttendanceGroupParam 删除信息
     * @return 删除结果
     */
    @Override
    public ResponseData delAttendanceGroup(DelAttendanceGroupParam delAttendanceGroupParam) {
        try {

            //考勤组删除
            attendanceGroupMapper.delAttendanceGroup(delAttendanceGroupParam);

            //用户考勤组重置
            employeeMapper.resetGroupId(delAttendanceGroupParam.getId());

            return new SuccessResponseData();
        } catch (Exception e) {
            log.error("Delete attendanceGroup error:{}", e.getMessage(), e);
        }
        return new ErrorResponseData(ErrorMessage.UPDATE_ACCOUNT_GROUP_ERROR_CODE, ErrorMessage.UPDATE_ACCOUNT_GROUP_ERROR_MSG);
    }
}
