<script setup>
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { usuarioService } from '@/services/usuarioService'

const router = useRouter()
const authStore = useAuthStore()

const handleLogout = async () => {
  try {
    // Faz a chamada ao servidor para invalidar o token
    await usuarioService.logout()
  } catch (e) {
    console.error('Erro ao fazer logout na API', e)
  } finally {
    // Independentemente do servidor, limpa o token local
    authStore.logout()
    router.push('/login')
  }
}
</script>

<template>
  <div class="home-container">
    <header class="top-nav">
      <div class="nav-content">
        <h1 class="logo">Instagram</h1>
        <nav class="actions">
          <RouterLink to="/configuracoes" class="btn-icon" title="Configurações">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width: 22px; height: 22px;"><circle cx="12" cy="12" r="3"></circle><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path></svg>
          </RouterLink>
          <RouterLink to="/perfil" class="nav-link">Meu Perfil</RouterLink>
          <button @click="handleLogout" class="btn-logout">Sair</button>
        </nav>
      </div>
    </header>

    <main class="feed-content">
      <div class="welcome-card">
        <h2>Bem-vindo ao Instagram</h2>
        <p>Este é o seu feed. Aqui você verá as postagens.</p>
      </div>
    </main>
  </div>
</template>

<style scoped>
.home-container {
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
  margin: 0;
}

.nav-link {
  color: var(--text-primary, #fff);
  text-decoration: none;
  font-weight: 600;
  padding: 0.5rem 1rem;
  border-radius: var(--border-radius-base, 4px);
  background-color: var(--color-brand, #0095f6);
  transition: background-color 0.3s ease;
}

.nav-link:hover {
  background-color: var(--color-brand-hover, #1877f2);
}

.actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.btn-icon {
  background: none;
  border: none;
  color: var(--text-secondary, #a8a8a8);
  cursor: pointer;
  padding: 0.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: color 0.2s, background-color 0.2s;
  text-decoration: none;
}

.btn-icon:hover {
  color: var(--text-primary, #fff);
  background-color: var(--bg-tertiary, #262626);
}

.btn-logout {
  background: transparent;
  color: var(--color-error, #ed4956);
  border: 1px solid var(--color-error, #ed4956);
  padding: 0.4rem 0.8rem;
  border-radius: var(--border-radius-base, 4px);
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s;
}

.btn-logout:hover {
  background: var(--color-error-bg, rgba(237, 73, 86, 0.1));
}

.feed-content {
  max-width: 600px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.welcome-card {
  background-color: var(--bg-secondary, #111);
  border: 1px solid var(--border-color, #333);
  border-radius: var(--border-radius-base, 8px);
  padding: 2rem;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.4);
}

.welcome-card h2 {
  margin-bottom: 1rem;
  font-weight: 600;
}

.welcome-card p {
  color: var(--text-secondary, #a8a8a8);
}
</style>
