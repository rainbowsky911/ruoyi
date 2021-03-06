import request from '@/utils/request'
import { parseStrEmpty } from '@/utils/ruoyi'

// 查询公司列表
export function listCompany(query) {
  return request({
    url: '/company/list',
    method: 'get',
    params: query
  })
}

// 查询公司列表
export function suggest(key) {
  return request({
    url: '/company/suggestion?key=' + key,
    method: 'get'
  })
}
