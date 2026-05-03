// Configuração central da API usando o Fetch nativo (Sem depender do Axios)
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const api = {
  async post(endpoint, data) {
    return fetch(`${baseURL}${endpoint}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
        // 'Authorization': 'Bearer ...' // Futuro interceptor nativo
      },
      body: JSON.stringify(data)
    })
  }

  // Métodos get, patch etc. poderão ser adicionados aqui seguindo o mesmo estilo:
  // async get(endpoint) { ... }
}

export default api
