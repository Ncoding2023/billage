import { useNavigate } from 'react-router-dom'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { ItemForm } from '@/components/ItemForm'
import { api } from '@/lib/api'

export function ItemCreatePage() {
  const navigate = useNavigate()

  async function handleSubmit(payload) {
    const { data } = await api.post('/items', payload)
    navigate(`/items/${data.itemNo}`)
  }

  return (
    <div className="mx-auto max-w-lg">
      <Card>
        <CardHeader>
          <CardTitle>물품 등록</CardTitle>
          <CardDescription>
            대여해줄 물품 정보와 거래 장소를 함께 입력해주세요.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <ItemForm onSubmit={handleSubmit} submitLabel="등록하기" />
        </CardContent>
      </Card>
    </div>
  )
}
