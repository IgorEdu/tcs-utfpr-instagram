import api from './api'

export const postService = {
  /**
   * Cria um novo post para o usuário logado
   */
  async criar(idUsuario, postData) {
    const response = await api.post(`/usuarios/${idUsuario}/posts`, postData)
    return response
  },

  /**
   * Obtém todos os posts de um usuário específico
   */
  async listar(idUsuario) {
    const response = await api.get(`/usuarios/${idUsuario}/posts`)
    return response
  },

  /**
   * Atualiza a legenda de um post
   */
  async atualizar(idUsuario, idPost, dados) {
    const response = await api.patch(`/usuarios/${idUsuario}/posts/${idPost}`, dados)
    return response
  },

  /**
   * Exclui (soft delete) um post
   */
  async deletar(idUsuario, idPost) {
    const response = await api.delete(`/usuarios/${idUsuario}/posts/${idPost}`)
    return response
  },

  /**
   * Curte ou descurte um post
   */
  async curtir(idDonoDoPost, idPost) {
    const response = await api.post(`/usuarios/${idDonoDoPost}/posts/${idPost}`, {})
    return response
  }
}
