import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { KakaoMapPicker } from '@/components/KakaoMapPicker'
import { CATEGORY_LABELS } from '@/lib/constants'

export function ItemForm({ initial, onSubmit, submitLabel }) {
  const [category, setCategory] = useState(initial?.category ?? '')
  const [form, setForm] = useState({
    itemName: initial?.itemName ?? '',
    description: initial?.description ?? '',
    rentalPoint: initial?.rentalPoint != null ? String(initial.rentalPoint) : '',
    rentalPlaceName: initial?.rentalPlaceName ?? '',
    rentalPlace: initial?.rentalPlace ?? '',
    rentalPlaceDetail: initial?.rentalPlaceDetail ?? '',
    latitude: initial?.latitude != null ? String(initial.latitude) : '',
    longitude: initial?.longitude != null ? String(initial.longitude) : '',
  })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  function handleChange(field) {
    return (e) => setForm((prev) => ({ ...prev, [field]: e.target.value }))
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')

    if (!category) {
      setError('카테고리를 선택해주세요.')
      return
    }

    if (!form.latitude || !form.longitude) {
      setError('지도에서 거래 장소를 선택해주세요.')
      return
    }

    setLoading(true)
    try {
      await onSubmit({
        category,
        itemName: form.itemName,
        description: form.description,
        rentalPoint: Number(form.rentalPoint),
        rentalPlaceName: form.rentalPlaceName,
        rentalPlace: form.rentalPlace,
        rentalPlaceDetail: form.rentalPlaceDetail || null,
        latitude: Number(form.latitude),
        longitude: Number(form.longitude),
      })
    } catch (err) {
      setError(err.response?.data?.message ?? '처리에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-4">
      <div className="flex flex-col gap-1.5">
        <Label htmlFor="category">카테고리</Label>
        <Select value={category} onValueChange={setCategory}>
          <SelectTrigger id="category" className="w-full">
            <SelectValue placeholder="카테고리를 선택하세요" />
          </SelectTrigger>
          <SelectContent>
            {Object.entries(CATEGORY_LABELS).map(([value, label]) => (
              <SelectItem key={value} value={value}>
                {label}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>
      <div className="flex flex-col gap-1.5">
        <Label htmlFor="itemName">물품명</Label>
        <Input id="itemName" value={form.itemName} onChange={handleChange('itemName')} required />
      </div>
      <div className="flex flex-col gap-1.5">
        <Label htmlFor="description">물품 설명</Label>
        <Textarea
          id="description"
          rows={4}
          value={form.description}
          onChange={handleChange('description')}
        />
      </div>
      <div className="flex flex-col gap-1.5">
        <Label htmlFor="rentalPoint">대여 포인트</Label>
        <Input
          id="rentalPoint"
          type="number"
          min={1}
          value={form.rentalPoint}
          onChange={handleChange('rentalPoint')}
          required
        />
      </div>

      <div className="border-t pt-4">
        <p className="mb-3 text-sm font-medium">거래 장소</p>
        <div className="flex flex-col gap-4">
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="rentalPlaceName">장소명</Label>
            <Input
              id="rentalPlaceName"
              placeholder="예: 우리집 앞"
              value={form.rentalPlaceName}
              onChange={handleChange('rentalPlaceName')}
              required
            />
          </div>
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="rentalPlace">주소</Label>
            <Input
              id="rentalPlace"
              placeholder="서울시 강남구 ..."
              value={form.rentalPlace}
              onChange={handleChange('rentalPlace')}
              required
            />
          </div>
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="rentalPlaceDetail">상세 주소 (선택)</Label>
            <Input
              id="rentalPlaceDetail"
              value={form.rentalPlaceDetail}
              onChange={handleChange('rentalPlaceDetail')}
            />
          </div>
          <div className="flex flex-col gap-1.5">
            <Label>지도에서 위치 선택</Label>
            <KakaoMapPicker
              latitude={form.latitude}
              longitude={form.longitude}
              onChange={(lat, lng, roadAddress) =>
                setForm((prev) => ({
                  ...prev,
                  latitude: String(lat),
                  longitude: String(lng),
                  rentalPlace: roadAddress || prev.rentalPlace,
                }))
              }
            />
          </div>
          <input type="hidden" id="latitude" value={form.latitude} readOnly />
          <input type="hidden" id="longitude" value={form.longitude} readOnly />
        </div>
      </div>

      {error && <p className="text-sm text-destructive">{error}</p>}
      <Button type="submit" disabled={loading} className="w-full">
        {loading ? '처리 중...' : submitLabel}
      </Button>
    </form>
  )
}
