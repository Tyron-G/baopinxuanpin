<template>
  <section>
    <PageHero eyebrow="团队协作" title="成员与选品任务分派" description="多人协作查看成员、分派机会页验证动作（P2 MVP）。" />
    <div class="grid">
      <section class="panel pad">
        <div class="head">
          <h2>团队成员</h2>
          <el-button type="primary" @click="showMember = true">添加成员</el-button>
        </div>
        <el-table :data="members" stripe size="small">
          <el-table-column prop="memberName" label="姓名" />
          <el-table-column prop="roleLabel" label="角色" />
          <el-table-column prop="permissionLevel" label="权限" width="100" />
          <el-table-column prop="accountId" label="账号" width="120" />
          <el-table-column prop="email" label="邮箱" />
        </el-table>
      </section>
      <section class="panel pad">
        <div class="head">
          <h2>任务分派</h2>
          <el-button type="primary" @click="showAssign = true">新建分派</el-button>
        </div>
        <el-table :data="assignments" stripe size="small">
          <el-table-column prop="actionTitle" label="动作" min-width="160" />
          <el-table-column prop="assigneeName" label="负责人" width="120" />
          <el-table-column prop="status" label="状态" width="100" />
          <el-table-column prop="approvalStatus" label="审批" width="100" />
          <el-table-column prop="approverName" label="审批人" width="100" />
          <el-table-column prop="cardId" label="cardId" width="90" />
          <el-table-column label="操作" width="160">
            <template #default="{ row }">
              <el-button
                v-if="row.approvalStatus === 'pending'"
                link
                type="primary"
                @click="approve(row)"
              >
                通过
              </el-button>
              <el-button
                v-if="row.approvalStatus === 'pending'"
                link
                type="danger"
                @click="reject(row)"
              >
                驳回
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </section>
    </div>

    <el-dialog v-model="showMember" title="添加成员" width="420px">
      <el-form label-width="80px">
        <el-form-item label="姓名"><el-input v-model="memberForm.memberName" /></el-form-item>
        <el-form-item label="角色"><el-input v-model="memberForm.roleLabel" /></el-form-item>
        <el-form-item label="权限">
          <el-select v-model="memberForm.permissionLevel" style="width: 100%">
            <el-option label="只读 viewer" value="viewer" />
            <el-option label="编辑 editor" value="editor" />
            <el-option label="管理员 admin" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item label="账号 ID"><el-input v-model="memberForm.accountId" placeholder="account-xxx" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="memberForm.email" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showMember = false">取消</el-button>
        <el-button type="primary" @click="saveMember">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showAssign" title="分派任务" width="480px">
      <el-form label-width="90px">
        <el-form-item label="动作标题"><el-input v-model="assignForm.actionTitle" /></el-form-item>
        <el-form-item label="负责人">
          <el-select v-model="assignForm.assigneeName" style="width: 100%">
            <el-option v-for="m in members" :key="m.id" :label="m.memberName" :value="m.memberName" />
          </el-select>
        </el-form-item>
        <el-form-item label="cardId"><el-input-number v-model="assignForm.cardId" :min="1" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="assignForm.note" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAssign = false">取消</el-button>
        <el-button type="primary" @click="saveAssign">分派</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { api } from '@/api'
import { getBrandId } from '@/composables/useBrandContext'
import type { TeamAssignmentItem, TeamMemberItem } from '@/types'
import PageHero from '@/components/common/PageHero.vue'
import { ElMessage } from 'element-plus'

const members = ref<TeamMemberItem[]>([])
const assignments = ref<TeamAssignmentItem[]>([])
const showMember = ref(false)
const showAssign = ref(false)
const memberForm = ref({ memberName: '', roleLabel: '电商运营', permissionLevel: 'editor', email: '', accountId: '' })
const assignForm = ref({ actionTitle: '', assigneeName: '', cardId: 1, note: '' })

async function load() {
  const brandId = getBrandId()
  const [memberRows, assignmentRows] = await Promise.all([
    api.getTeamMembers(brandId),
    api.getTeamAssignments(brandId)
  ])
  members.value = memberRows
  assignments.value = assignmentRows
}

async function saveMember() {
  await api.addTeamMember(getBrandId(), memberForm.value)
  showMember.value = false
  ElMessage.success('成员已添加')
  await load()
}

async function saveAssign() {
  await api.assignTeamTask(getBrandId(), {
    ...assignForm.value,
    status: '待处理'
  })
  showAssign.value = false
  ElMessage.success('任务已分派')
  await load()
}

async function approve(row: TeamAssignmentItem) {
  await api.approveTeamAssignment(getBrandId(), row.id, '陈负责人')
  ElMessage.success('已通过审批')
  await load()
}

async function reject(row: TeamAssignmentItem) {
  await api.rejectTeamAssignment(getBrandId(), row.id, '陈负责人', '需补充验证数据')
  ElMessage.success('已驳回')
  await load()
}

onMounted(load)
</script>

<style scoped>
.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
@media (max-width: 960px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
