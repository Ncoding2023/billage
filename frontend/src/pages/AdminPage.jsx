import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { AdminInquiriesTab } from '@/components/admin/AdminInquiriesTab'
import { AdminItemsTab } from '@/components/admin/AdminItemsTab'
import { AdminMembersTab } from '@/components/admin/AdminMembersTab'
import { AdminRentalsTab } from '@/components/admin/AdminRentalsTab'

export function AdminPage() {
  return (
    <div className="mx-auto flex max-w-3xl flex-col gap-6">
      <h1 className="text-xl font-semibold">관리자 페이지</h1>

      <Tabs defaultValue="members">
        <TabsList>
          <TabsTrigger value="members">회원 관리</TabsTrigger>
          <TabsTrigger value="items">물품 관리</TabsTrigger>
          <TabsTrigger value="rentals">대여 현황</TabsTrigger>
          <TabsTrigger value="inquiries">문의 관리</TabsTrigger>
        </TabsList>

        <TabsContent value="members">
          <AdminMembersTab />
        </TabsContent>
        <TabsContent value="items">
          <AdminItemsTab />
        </TabsContent>
        <TabsContent value="rentals">
          <AdminRentalsTab />
        </TabsContent>
        <TabsContent value="inquiries">
          <AdminInquiriesTab />
        </TabsContent>
      </Tabs>
    </div>
  )
}
