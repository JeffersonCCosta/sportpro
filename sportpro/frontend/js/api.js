/**
 * api.js — Módulo de comunicação com o backend SportPro
 *
 * Centraliza todas as chamadas HTTP para a API REST.
 * Usa a Fetch API nativa do browser.
 *
 * FLUXO:
 *  Frontend → fetch() → API REST (Spring Boot) → MySQL / n8n
 */

const API_BASE = 'http://localhost:8080/api';

/**
 * Função base de requisição HTTP.
 * Adiciona headers padrão e trata erros de forma centralizada.
 *
 * @param {string} endpoint - Caminho relativo (ex: '/treinadores')
 * @param {object} options  - Opções do fetch (method, body, etc.)
 * @returns {Promise<object>} - JSON retornado pela API
 */
async function request(endpoint, options = {}) {
  const url = `${API_BASE}${endpoint}`;

  const defaultHeaders = {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  };

  const config = {
    ...options,
    headers: { ...defaultHeaders, ...options.headers }
  };

  try {
    const response = await fetch(url, config);
    const data = await response.json();

    if (!response.ok) {
      // Lança erro com a mensagem retornada pelo GlobalExceptionHandler
      throw new Error(data.message || `Erro HTTP: ${response.status}`);
    }

    return data;
  } catch (error) {
    // Erro de rede (backend offline, CORS etc.)
    if (error.name === 'TypeError') {
      throw new Error('Não foi possível conectar ao servidor. Verifique se o backend está rodando.');
    }
    throw error;
  }
}

// ================================================================
// Endpoints — Autenticação
// ================================================================

/**
 * Login de treinador ou atleta.
 * @param {string} email
 * @param {string} senha
 * @param {string} tipo - 'TREINADOR' ou 'ATLETA'
 */
const Auth = {
  login: (email, senha, tipo) =>
    request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, senha, tipo })
    })
};

// ================================================================
// Endpoints — Treinadores
// ================================================================
const TreinadorAPI = {
  cadastrar: (data) => request('/treinadores', { method: 'POST', body: JSON.stringify(data) }),
  listar: () => request('/treinadores'),
  buscarPorId: (id) => request(`/treinadores/${id}`)
};

// ================================================================
// Endpoints — Atletas
// ================================================================
const AtletaAPI = {
  cadastrar: (data) => request('/atletas', { method: 'POST', body: JSON.stringify(data) }),
  listar: () => request('/atletas'),
  buscarPorId: (id) => request(`/atletas/${id}`),
  atualizarPerfil: (data) => request('/atletas/perfil', { method: 'PUT', body: JSON.stringify(data) }),
  gerarCronograma: (id) => request(`/atletas/${id}/cronograma`, { method: 'POST' }),
  listarCronogramas: (id) => request(`/atletas/${id}/cronogramas`),
  listarPorTreinador: (treinadorId) => request(`/atletas/treinador/${treinadorId}`)
};

// ================================================================
// Endpoints — Modalidades
// ================================================================
const ModalidadeAPI = {
  cadastrar: (data) => request('/modalidades', { method: 'POST', body: JSON.stringify(data) }),
  listar: () => request('/modalidades'),
  listarPorTreinador: (id) => request(`/modalidades/treinador/${id}`)
};

// ================================================================
// Endpoints — Metodologias
// ================================================================
const MetodologiaAPI = {
  cadastrar: (data) => request('/metodologias', { method: 'POST', body: JSON.stringify(data) }),
  listarPorTreinador: (id) => request(`/metodologias/treinador/${id}`)
};

// ================================================================
// Utilitários de sessão (localStorage)
// ================================================================

/**
 * Salva dados do usuário logado no localStorage.
 * Em produção: usar httpOnly cookies com JWT.
 */
const Session = {
  save: (userData) => localStorage.setItem('sportpro_user', JSON.stringify(userData)),

  get: () => {
    const data = localStorage.getItem('sportpro_user');
    return data ? JSON.parse(data) : null;
  },

  clear: () => localStorage.removeItem('sportpro_user'),

  /** Verifica se há usuário logado e redireciona se necessário */
  requireAuth: (tipo = null) => {
    const user = Session.get();
    if (!user) {
      window.location.href = '/frontend/pages/login.html';
      return null;
    }
    if (tipo && user.data.tipo !== tipo) {
      window.location.href = '/frontend/pages/login.html';
      return null;
    }
    return user.data;
  }
};

// ================================================================
// Utilitários de UI
// ================================================================
const UI = {
  /** Exibe mensagem de feedback ao usuário */
  showAlert: (message, type = 'success', container = null) => {
    const el = document.createElement('div');
    el.className = `alert alert-${type} mt-2`;
    el.textContent = message;

    const target = container || document.querySelector('.alert-container') || document.body;
    target.prepend(el);

    setTimeout(() => el.remove(), 4000);
  },

  /** Ativa loading em botão */
  setLoading: (btn, loading) => {
    if (loading) {
      btn.dataset.original = btn.innerHTML;
      btn.innerHTML = '<span class="spinner"></span> Aguarde...';
      btn.disabled = true;
    } else {
      btn.innerHTML = btn.dataset.original;
      btn.disabled = false;
    }
  },

  /** Formata data para pt-BR */
  formatDate: (isoString) => {
    if (!isoString) return '—';
    return new Date(isoString).toLocaleDateString('pt-BR', {
      day: '2-digit', month: '2-digit', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  },

  /** Retorna iniciais do nome */
  initials: (name) => name ? name.split(' ').slice(0,2).map(n => n[0]).join('').toUpperCase() : '?'
};
