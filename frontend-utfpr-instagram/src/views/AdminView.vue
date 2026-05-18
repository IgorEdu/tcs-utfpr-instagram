<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { usuarioService } from '@/services/usuarioService'

const usuarios = ref([])
const isLoading = ref(true)
const errorMessage = ref(null)
const isDeleting = ref({})

const handleDeleteUser = async (user) => {
  if (!window.confirm(`Tem certeza que deseja excluir o usuário @${user.usuario}? Esta ação é irreversível.`)) {
    return
  }

  isDeleting.value[user.id] = true
  
  try {
    const resp = await usuarioService.excluir(user.id)
    if (resp.ok) {
      usuarios.value = usuarios.value.filter(u => u.id !== user.id)
    } else {
      const errData = await resp.json().catch(() => null)
      alert(errData?.mensagem || 'Falha ao excluir o usuário.')
    }
  } catch (error) {
    alert('Erro de comunicação ao tentar excluir o usuário.')
  } finally {
    isDeleting.value[user.id] = false
  }
}

onMounted(async () => {
  try {
    const resp = await usuarioService.listar()
    if (resp.ok) {
      const data = await resp.json()
      usuarios.value = data.dados?.usuarios || []
    } else {
      errorMessage.value = 'Falha ao carregar usuários.'
    }
  } catch (error) {
    errorMessage.value = 'Erro de comunicação.'
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <div class="admin-container">
    <header class="top-nav">
      <div class="nav-content">
        <RouterLink to="/home" class="logo">Instagram</RouterLink>
        <nav class="actions">
          <RouterLink to="/home" class="btn-cancel">Voltar</RouterLink>
        </nav>
      </div>
    </header>

    <main class="admin-content">
      <div class="header-title">
        <h2>Painel Administrativo</h2>
        <p class="text-secondary">Gerencie os usuários do sistema</p>
      </div>

      <div v-if="isLoading" class="loader">Carregando usuários...</div>
      <div v-else-if="errorMessage" class="error-msg">{{ errorMessage }}</div>
      
      <div v-else class="users-list">
        <div v-for="user in usuarios" :key="user.id" class="user-card">
          <div class="user-info">
            <div class="avatar-sm"></div>
            <div class="user-details">
              <h3>{{ user.nome || 'Sem nome' }}</h3>
              <p>@{{ user.usuario }} <span class="user-id">(ID: {{ user.id }})</span></p>
            </div>
          </div>
          <div class="user-actions">
            <RouterLink :to="'/perfil/' + user.id" class="btn-save">Ver Perfil</RouterLink>
            <button @click="handleDeleteUser(user)" class="btn-delete" :disabled="isDeleting[user.id]">
              {{ isDeleting[user.id] ? 'Excluindo...' : 'Excluir' }}
            </button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.admin-container {
  min-height: 100vh;
  background-color: var(--bg-primary, #000);
  color: var(--text-primary, #fff);
}

.top-nav {
  border-bottom: 1px solid var(--border-color, #333);
  background-color: var(--bg-secondary, #111);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-content {
  max-width: 935px;
  margin: 0 auto;
  padding: 0 1rem;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  font-family: 'Times New Roman', serif;
  font-size: 1.5rem;
  color: var(--text-primary, #fff);
  text-decoration: none;
  margin: 0;
}

.actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.btn-cancel {
  background-color: transparent;
  color: var(--text-primary, #fff);
  border: 1px solid var(--border-color, #333);
  padding: 0.4rem 0.8rem;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  font-size: 0.9rem;
}

.btn-cancel:hover {
  background-color: var(--bg-tertiary, #262626);
}

.admin-content {
  max-width: 935px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.header-title {
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--border-color, #333);
}

.header-title h2 {
  font-size: 1.75rem;
  font-weight: 300;
  margin: 0 0 0.5rem 0;
}

.text-secondary {
  color: var(--text-secondary, #a8a8a8);
  font-size: 1.1rem;
  margin: 0;
}

.loader {
  color: var(--color-brand, #0095f6);
  font-size: 1.1rem;
  font-weight: 500;
  text-align: center;
  margin-top: 2rem;
  animation: pulse 1.5s infinite ease-in-out;
}

@keyframes pulse {
  0%, 100% { opacity: 0.6; }
  50% { opacity: 1; }
}

.error-msg {
  color: var(--color-error, #ed4956);
  background: var(--color-error-bg, rgba(237, 73, 86, 0.1));
  padding: 0.75rem;
  border-radius: var(--border-radius-base, 8px);
  font-size: 0.95rem;
  border: 1px solid var(--color-error, #ed4956);
  text-align: center;
}

.users-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.user-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: var(--bg-secondary, #111);
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid var(--border-color, #333);
  transition: transform 0.2s, box-shadow 0.2s;
}

.user-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.5);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.avatar-sm {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: var(--bg-tertiary, #262626);
  border: 1px solid var(--border-color, #333);
}

.user-details h3 {
  margin: 0 0 0.25rem 0;
  font-size: 1.1rem;
  font-weight: 500;
}

.user-details p {
  margin: 0;
  color: var(--text-secondary, #a8a8a8);
  font-size: 0.9rem;
}

.user-id {
  font-size: 0.8rem;
  opacity: 0.7;
}

.btn-save {
  background-color: var(--color-brand, #0095f6);
  color: #fff;
  border: none;
  padding: 0.4rem 0.8rem;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  font-size: 0.9rem;
  transition: background-color 0.2s;
}

.btn-save:hover {
  background-color: var(--color-brand-hover, #1877f2);
}

.user-actions {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.btn-delete {
  background-color: transparent;
  color: var(--color-error, #ed4956);
  border: 1px solid var(--color-error, #ed4956);
  padding: 0.4rem 0.8rem;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.btn-delete:hover:not(:disabled) {
  background-color: var(--color-error, #ed4956);
  color: #fff;
}

.btn-delete:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
