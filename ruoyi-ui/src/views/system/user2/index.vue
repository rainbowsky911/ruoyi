<template>
  <div class="app-container">
    <el-row>

      <el-col :span="4" :xs="24">
      </el-col>
      <el-col :span="20" :xs="24" style="width: 100%">

        <!-- 搜索框       -->
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch"
                 label-width="68px"
        >
          <el-form-item label="公司名称" prop="key">
            <el-col :span="24">
              <el-autocomplete
                v-model="queryParams.key"
                :fetch-suggestions="querySearchAsync"
                placeholder="请输入内容"
                @select="handleSelect"
              ></el-autocomplete>
            </el-col>

          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="searchCompany">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </el-form-item>
          <el-form-item>
            <el-dropdown @command="sortedCommand">
      <span class="el-dropdown-link">
        排序方式<i class="el-icon-arrow-down el-icon--right"></i>
      </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="0">默认排序[时间]</el-dropdown-item>
                <el-dropdown-item command="1">留言数量</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>

          </el-form-item>
        </el-form>


        <!--  修改按钮      -->
        <el-row :gutter="15" style="margin-top: 3px">

          <el-col :span="1.5">
            <el-button
              type="primary"
              plain
              icon="el-icon-plus"
              size="mini"
              @click="handleAdd"
              v-hasPermi="['system:user:add']"
            >新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="success"
              plain
              icon="el-icon-edit"
              size="mini"
              :disabled="single"
              @click="handleUpdate"
              v-hasPermi="['system:user:edit']"
            >修改
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="danger"
              plain
              icon="el-icon-delete"
              size="mini"
              :disabled="multiple"
              @click="handleDelete"
              v-hasPermi="['system:user:remove']"
            >删除
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="info"
              plain
              icon="el-icon-upload2"
              size="mini"
              @click="handleImport"
              v-hasPermi="['system:user:import']"
            >导入
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="warning"
              plain
              icon="el-icon-download"
              size="mini"
              @click="handleExport"
              v-hasPermi="['system:user:export']"
            >导出
            </el-button>
          </el-col>

        </el-row>


        <!-- 表格主体         -->
        <el-table v-loading="loading"
                  :data="companyList"
                  style="margin-top: 30px;"
                  @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="30" align="center"/>

          <el-table-column label="公司编号" align="center" key="id" prop="id" v-if="companyColumns[0].visible"/>
          <el-table-column label="公司名称" align="center" key="title" prop="title" v-if="companyColumns[1].visible"
                           :show-overflow-tooltip="true"
          />
          <el-table-column label="留言条数" align="center" key="commentCount" prop="commentCount"
                           v-if="companyColumns[2].visible"
                           :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              <span class="el-tag el-tag--medium el-tag--success">{{ scope.row.commentCount }}</span>
            </template>

          </el-table-column>
          <el-table-column label="评价" align="center" key="description" prop="description"
                           v-if="companyColumns[3].visible"
                           :show-overflow-tooltip="true"
          />
          <el-table-column label="评价展开" align="center"
                           v-if="companyColumns[4].visible"
                           width="120"
          >
            <el-popover
              placement="top-start"
              title="标题"
              width="200"
              trigger="hover"
              content="zzz"
            >
              <el-button slot="reference">hover 激活</el-button>
            </el-popover>
          </el-table-column>
          <el-table-column label="留言详情" align="center" key="commentList" v-if="companyColumns[5].visible">
            <template slot-scope="scope">
              <el-button type="success"
                         size="small"
                         v-if="scope.row.commentList!=null"
                         @click="showCommentList(scope.row.commentList)"
              >
                查看留言
              </el-button>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" key="createTime" prop="createTime"
                           v-if="companyColumns[4].visible"
                           width="120"
          />

        </el-table>
        <!-- 分页       -->
        <pagination
          v-show="companyTotal>0"
          :total="companyTotal"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getCompanyList"
        />

      </el-col>
    </el-row>


    <el-dialog :title="title" :visible.sync="companyOpen" width="600px" append-to-body>

      <div slot="header" class="clearfix">
      </div>
      <ul v-for="o in commentList" :key="o" class="text item" style="margin-bottom: 33px">
        <li> {{ o }}</li>
      </ul>
      <span slot="footer" class="dialog-footer">
    <el-button @click="companyOpen =!companyOpen">返 回</el-button>
  </span>
    </el-dialog>


    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户昵称">
              <el-input v-model="form.nickName" placeholder="审批人"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归属部门">
              <el-input v-model="form.deptId" placeholder="归属部门"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="手机号码">
              <el-input v-model="form.phonenumber" placeholder="手机号码"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="邮箱"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="用户性别">
              <el-select v-model="form.sex" placeholder="请选择">
                <el-option
                  v-for="dict in dict.type.sys_user_sex"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in dict.type.sys_normal_disable"
                  :key="dict.value"
                  :label="dict.value"
                >{{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="岗位">
              <el-select v-model="form.postIds" multiple placeholder="请选择岗位">
                <el-option
                  v-for="item in postOptions"
                  :key="item.postId"
                  :label="item.postName"
                  :value="item.postId"
                  :disabled="item.status == 1"
                ></el-option>
              </el-select>
            </el-form-item>

          </el-col>
          <el-col :span="12">
            <el-form-item label="角色">
              <el-select v-model="form.roleIds" multiple placeholder="请选择角色">
                <el-option
                  v-for="item in roleOptions"
                  :key="item.roleId"
                  :label="item.roleName"
                  :value="item.roleName"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input type="textarea" v-model="form.remark" placeholder="输入备注"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="open = !open">取 消</el-button>
        <el-button @click="updateForm" type="primary">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<style>

.el-tooltip__popper {
  max-width: 20%;
}
</style>


<style>
.text {
  font-size: 14px;
}

.item {
  margin-bottom: 18px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both
}

.box-card {
  width: 480px;
}
</style>

<style>
.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
}

.el-icon-arrow-down {
  font-size: 12px;
}
</style>
<script>
import { addUser, changeUserStatus, delUser, getUser, listUser, resetUserPwd, updateUser } from '@/api/system/user'
import { listCompany, suggest } from '@/api/system/company'
import { getToken } from '@/utils/auth'
import { treeselect } from '@/api/system/dept'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import item from '@/layout/components/Sidebar/Item'
import axios from 'axios'

export default {
  name: 'User',
  dicts: ['sys_normal_disable', 'sys_user_sex'],
  components: { Treeselect },
  data() {
    return {
      restaurants: [],
      state1: '',
      state: '',

      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 用户表格数据
      userList: null,

      //公司表格数据
      companyList: null,
      timeout: null,
      //公司条数
      companyTotal: 0,
      //搜索建议
      suggestion: [],
      //公司是否显示
      companyOpen: false,

      //留言内容
      commentList: [],

      ops: [],
      showOps: false,
      // 弹出层标题
      title: undefined,
      // 部门树选项
      deptOptions: undefined,
      // 是否显示弹出层
      open: false,
      // 部门名称
      deptName: undefined,
      // 默认密码
      initPassword: undefined,
      // 日期范围
      dateRange: [],
      // 岗位选项
      postOptions: [],
      // 角色选项
      roleOptions: [],
      // 表单参数
      form: {},
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      // 用户导入参数
      upload: {
        // 是否显示弹出层（用户导入）
        open: false,
        // 弹出层标题（用户导入）
        title: '',
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的用户数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { Authorization: 'Bearer ' + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + '/system/user/importData'
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        phonenumber: undefined,
        status: undefined,
        deptId: undefined,
        title: undefined,
        sortType: 0,
        key: undefined

      },
      // 列信息
      columns: [
        { key: 0, label: `用户编号`, visible: true },
        { key: 1, label: `用户名称`, visible: true },
        { key: 2, label: `用户昵称`, visible: true },
        { key: 3, label: `部门`, visible: true },
        { key: 4, label: `手机号码`, visible: true },
        { key: 5, label: `状态`, visible: true },
        { key: 6, label: `创建时间`, visible: true }
      ],
      // 列信息
      companyColumns: [
        { key: 0, label: `编号`, visible: true },
        { key: 1, label: `公司名称`, visible: true },
        { key: 2, label: `留言条数`, visible: true },
        { key: 3, label: `评价`, visible: true },
        { key: 4, label: `创建时间`, visible: true },
        { key: 5, label: `留言详情`, visible: true }
      ],

      // 表单校验
      rules: {
        userName: [
          { required: true, message: '用户名称不能为空', trigger: 'blur' },
          { min: 2, max: 20, message: '用户名称长度必须介于 2 和 20 之间', trigger: 'blur' }
        ],
        nickName: [
          { required: true, message: '用户昵称不能为空', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '用户密码不能为空', trigger: 'blur' },
          { min: 5, max: 20, message: '用户密码长度必须介于 5 和 20 之间', trigger: 'blur' }
        ],
        email: [
          {
            type: 'email',
            message: '请输入正确的邮箱地址',
            trigger: ['blur', 'change']
          }
        ],
        phonenumber: [
          {
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            message: '请输入正确的手机号码',
            trigger: 'blur'
          }
        ]
      }
    }
  },
  watch: {
    // 根据名称筛选部门树
    deptName(val) {
      this.$refs.tree.filter(val)
    }
  },
  created() {
    this.getList()
    this.getCompanyList()
    this.getTreeselect()
    this.getConfigKey('sys.user.initPassword').then(response => {
      this.initPassword = response.msg
    })
  },
  methods: {

    /**搜索建议**/
    querySearchAsync(queryString, cb) {
      clearTimeout(this.timer)
      this.timer = setTimeout(async() => {
        const res = await suggest(this.queryParams.key)
        this.suggestion = []
        for (let item of res) {
          // this.suggestion.value = item
          this.suggestion.push({ 'value': item })
        }
        console.log(this.suggestion)
        cb(this.suggestion)
      }, 100)
    },

    loadAll() {
      return [
        { 'value': '三全鲜食（北新泾店）', 'address': '长宁区新渔路144号' },
        { 'value': '十二泷町', 'address': '上海市北翟路1444弄81号B幢-107' },
        { 'value': '十二四町', 'address': '上海市北翟路1444弄81号B幢-107' },
        { 'value': '星移浓缩咖啡', 'address': '上海市嘉定区新郁路817号' },
        { 'value': '阿姨奶茶/豪大大', 'address': '嘉定区曹安路1611号' },
        { 'value': '开心丽果（缤谷店）', 'address': '上海市长宁区威宁路天山路341号' },
        { 'value': '超级鸡车（丰庄路店）', 'address': '上海市嘉定区丰庄路240号' }
      ]
    }
    ,
    handleSelect(item) {
    }
    ,

    /* *排序* */
    sortedCommand(command) {
      this.companyList = []
      this.queryParams.sortType = command
      this.getCompanyList()
    }
    ,

    /*搜索*/
    search() {
      this.queryParams.pageNum = 1
      this.getList()
    }
    ,

    /*搜索公司*/
    searchCompany() {
      this.queryParams.title = this.queryParams.key
      this.queryParams.key = undefined
      this.queryParams.pageNum = 1
      this.getCompanyList()
    }
    ,

    /*修改用户信息*/
    updateForm() {
      if (this.form.userId != undefined) {
        updateUser(this.form).then(response => {
          this.$modal.msgSuccess('修改成功')
          this.open = false
          this.getList()
        })
      }
    }
    ,
    /**去除HTML标签**/
    delHtmlTag(str) {
      return str.replace(/<[^>]+>/g, '')//去掉所有的html标记
    }
    ,

    /** 查询用户列表 */
    getList() {
      this.loading = true
      listUser(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          this.userList = response.rows
          this.total = response.total
          this.loading = false
        }
      )
    }
    ,
    /** 查询公司列表 */
    getCompanyList() {
      this.loading = true
      listCompany(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          response.rows.map(item => {
            item.description = this.delHtmlTag(item.description)
            if (item.commentList !== null) {
              item.commentList.map(e => {
                e.replace(/<[^>]+>/g, '')
              })
            }
          })
          response.code = '200'
          response.msg = '查询成功'
          this.companyList = response.rows
          this.companyTotal = response.total
          this.loading = false
          return item

        }
      )
    }
    ,

    /**查看留言**/
    showCommentList(row) {
      this.companyOpen = true
      this.commentList = row
    }
    ,

    /** 查询部门下拉树结构 */
    getTreeselect() {
      treeselect().then(response => {
        this.deptOptions = response.data
      })
    }
    ,
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true
      return data.label.indexOf(value) !== -1
    }
    ,
    // 节点单击事件
    handleNodeClick(data) {
      this.queryParams.deptId = data.id
      this.handleQuery()
    }
    ,
    // 用户状态修改
    handleStatusChange(row) {
      let text = row.status === '0' ? '启用' : '停用'
      this.$modal.confirm('确认要"' + text + '""' + row.userName + '"用户吗？').then(function() {
        return changeUserStatus(row.userId, row.status)
      }).then(() => {
        this.$modal.msgSuccess(text + '成功')
      }).catch(function() {
        row.status = row.status === '0' ? '1' : '0'
      })
    }
    ,
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    }
    ,
    // 表单重置
    reset() {
      this.form = {
        userId: undefined,
        deptId: undefined,
        userName: undefined,
        nickName: undefined,
        password: undefined,
        phonenumber: undefined,
        email: undefined,
        sex: undefined,
        status: '0',
        remark: undefined,
        postIds: [],
        roleIds: []
      }
      this.resetForm('form')
    }
    ,
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getCompanyList()
    }
    ,
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = []
      this.resetForm('queryForm')
      this.handleQuery()
    }
    ,
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length != 1
      this.multiple = !selection.length
    }
    ,
    // 更多操作触发
    handleCommand(command, row) {
      switch (command) {
        case 'handleResetPwd':
          this.handleResetPwd(row)
          break
        case 'handleAuthRole':
          this.handleAuthRole(row)
          break
        default:
          break
      }
    }
    ,
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.getTreeselect()
      getUser().then(response => {
        this.postOptions = response.posts
        this.roleOptions = response.roles
        this.open = true
        this.title = '添加用户'
        this.form.password = this.initPassword
      })
    }
    ,
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const userId = row.userId || this.ids
      getUser(userId).then(response => {
        this.form = response.data
        this.postOptions = response.posts
        this.roleOptions = response.roles
        this.form.postIds = response.postIds
        this.form.roleIds = response.roleIds
        this.open = true
        this.title = '修改用户'
        this.form.password = ''
      })
    }
    ,
    /** 重置密码按钮操作 */
    handleResetPwd(row) {
      this.$prompt('请输入"' + row.userName + '"的新密码', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        closeOnClickModal: false,
        inputPattern: /^.{5,20}$/,
        inputErrorMessage: '用户密码长度必须介于 5 和 20 之间'
      }).then(({ value }) => {
        resetUserPwd(row.userId, value).then(response => {
          this.$modal.msgSuccess('修改成功，新密码是：' + value)
        })
      }).catch(() => {
      })
    }
    ,
    /** 分配角色操作 */
    handleAuthRole: function(row) {
      const userId = row.userId
      this.$router.push('/system/user-auth/role/' + userId)
    }
    ,
    /** 提交按钮 */
    submitForm: function() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.userId != undefined) {
            updateUser(this.form).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addUser(this.form).then(response => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    }
    ,
    /** 删除按钮操作 */
    handleDelete(row) {
      const userIds = row.userId || this.ids
      this.$modal.confirm('是否确认删除用户编号为"' + userIds + '"的数据项？').then(function() {
        return delUser(userIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {
      })
    }
    ,
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/user/export', {
        ...this.queryParams
      }, `user_${new Date().getTime()}.xlsx`)
    }
    ,
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = '用户导入'
      this.upload.open = true
    }
    ,
    /** 下载模板操作 */
    importTemplate() {
      this.download('system/user/importTemplate', {}, `user_template_${new Date().getTime()}.xlsx`)
    }
    ,
    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true
    }
    ,
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false
      this.upload.isUploading = false
      this.$refs.upload.clearFiles()
      this.$alert('<div style=\'overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;\'>' + response.msg + '</div>', '导入结果', { dangerouslyUseHTMLString: true })
      this.getList()
    }
    ,
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit()
    }

  },
  mounted() {
    this.restaurants = this.loadAll()
    // suggest(this.queryParams.key).then(response => {
    //   this.restaurants = response
    // })
  }
}


</script>
