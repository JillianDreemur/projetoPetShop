import { useEffect, useState } from 'react';

const API = 'http://localhost:8080';

const petInicial = { nome: '', raca: '', nomeDono: '', quantidadeVisitas: 0 };
const agendamentoInicial = { data: '', tipoServico: '', petId: '' };

export default function App() {
  const [pets, setPets] = useState([]);
  const [agendamentos, setAgendamentos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [petForm, setPetForm] = useState(petInicial);
  const [petEditId, setPetEditId] = useState('');
  const [agendamentoForm, setAgendamentoForm] = useState(agendamentoInicial);
  const [agendamentoEditId, setAgendamentoEditId] = useState('');

  async function carregarDados() {
    try {
      setLoading(true);
      const [petsRes, agendamentosRes] = await Promise.all([
        fetch(`${API}/pets`),
        fetch(`${API}/agendamentos`)
      ]);

      if (!petsRes.ok || !agendamentosRes.ok) {
        throw new Error('Não foi possível carregar os dados da API.');
      }

      setPets(await petsRes.json());
      setAgendamentos(await agendamentosRes.json());
      setError('');
    } catch (err) {
      setError(err.message || 'Erro ao carregar dados.');
    } finally {
      setLoading(false);
    }
  }

  async function requestJson(url, method, body) {
    const options = {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: body ? JSON.stringify(body) : undefined
    };

    const response = await fetch(url, options);
    const result = await response.json().catch(() => null);

    if (!response.ok) {
      throw new Error(result?.error || 'Erro na operação.');
    }

    return result;
  }

  async function salvarPet(event) {
    event.preventDefault();
    try {
      if (petEditId) {
        await requestJson(`${API}/pets/${petEditId}`, 'PUT', {
          ...petForm,
          quantidadeVisitas: Number(petForm.quantidadeVisitas || 0)
        });
      } else {
        await requestJson(`${API}/pets`, 'POST', {
          ...petForm,
          quantidadeVisitas: Number(petForm.quantidadeVisitas || 0)
        });
      }
      setPetForm(petInicial);
      setPetEditId('');
      await carregarDados();
    } catch (err) {
      setError(err.message || 'Erro ao salvar pet.');
    }
  }

  async function excluirPet(id) {
    try {
      await fetch(`${API}/pets/${id}`, { method: 'DELETE' });
      await carregarDados();
    } catch (err) {
      setError(err.message || 'Erro ao excluir pet.');
    }
  }

  async function selecionarPet(pet) {
    setPetEditId(pet.id);
    setPetForm({
      nome: pet.nome,
      raca: pet.raca,
      nomeDono: pet.nomeDono,
      quantidadeVisitas: pet.quantidadeVisitas ?? 0
    });
  }

  async function salvarAgendamento(event) {
    event.preventDefault();
    try {
      if (agendamentoEditId) {
        await requestJson(`${API}/agendamentos/${agendamentoEditId}`, 'PUT', {
          ...agendamentoForm,
          data: new Date(agendamentoForm.data).toISOString()
        });
      } else {
        await requestJson(`${API}/agendamentos`, 'POST', {
          ...agendamentoForm,
          data: new Date(agendamentoForm.data).toISOString()
        });
      }
      setAgendamentoForm(agendamentoInicial);
      setAgendamentoEditId('');
      await carregarDados();
    } catch (err) {
      setError(err.message || 'Erro ao salvar agendamento.');
    }
  }

  async function excluirAgendamento(id) {
    try {
      await fetch(`${API}/agendamentos/${id}`, { method: 'DELETE' });
      await carregarDados();
    } catch (err) {
      setError(err.message || 'Erro ao excluir agendamento.');
    }
  }

  async function selecionarAgendamento(ag) {
    setAgendamentoEditId(ag.id);
    setAgendamentoForm({
      data: ag.data ? ag.data.slice(0, 16) : '',
      tipoServico: ag.tipoServico,
      petId: ag.petId
    });
  }

  useEffect(() => {
    carregarDados();
  }, []);

  return (
    <div className="app-shell">
      <header className="hero-card">
        <div>
          <p className="eyebrow">Pet Shop • Microsserviços</p>
          <h1>Painel completo do Pet Shop</h1>
          <p className="subtitle">Cadastre, edite e remova pets e agendamentos direto pelo frontend, passando pelo gateway e pelos microsserviços.</p>
        </div>
        <button className="refresh-btn" onClick={carregarDados}>Atualizar</button>
      </header>

      {error && <div className="alert">{error}</div>}

      <section className="grid">
        <article className="panel">
          <div className="panel-header">
            <h2>Pets</h2>
            <span>{pets.length}</span>
          </div>

          <form onSubmit={salvarPet} className="form-card">
            <input value={petForm.nome} onChange={(e) => setPetForm({ ...petForm, nome: e.target.value })} placeholder="Nome do pet" required />
            <input value={petForm.raca} onChange={(e) => setPetForm({ ...petForm, raca: e.target.value })} placeholder="Raça" required />
            <input value={petForm.nomeDono} onChange={(e) => setPetForm({ ...petForm, nomeDono: e.target.value })} placeholder="Nome do dono" required />
            <input type="number" min="0" value={petForm.quantidadeVisitas} onChange={(e) => setPetForm({ ...petForm, quantidadeVisitas: e.target.value })} placeholder="Visitas" />
            <button className="primary-btn" type="submit">{petEditId ? 'Atualizar pet' : 'Adicionar pet'}</button>
            {petEditId && <button className="ghost-btn" type="button" onClick={() => { setPetEditId(''); setPetForm(petInicial); }}>Cancelar edição</button>}
          </form>

          {loading ? <p>Carregando...</p> : pets.length === 0 ? <p>Nenhum pet encontrado.</p> : (
            <ul className="card-list">
              {pets.map((pet) => (
                <li key={pet.id} className="card-item">
                  <strong>{pet.nome}</strong>
                  <p>Raça: {pet.raca}</p>
                  <p>Dono: {pet.nomeDono}</p>
                  <p>Visitas: {pet.quantidadeVisitas ?? 0}</p>
                  <div className="actions-row">
                    <button className="ghost-btn" onClick={() => selecionarPet(pet)}>Editar</button>
                    <button className="danger-btn" onClick={() => excluirPet(pet.id)}>Excluir</button>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </article>

        <article className="panel">
          <div className="panel-header">
            <h2>Agendamentos</h2>
            <span>{agendamentos.length}</span>
          </div>

          <form onSubmit={salvarAgendamento} className="form-card">
            <input type="datetime-local" value={agendamentoForm.data} onChange={(e) => setAgendamentoForm({ ...agendamentoForm, data: e.target.value })} required />
            <input value={agendamentoForm.tipoServico} onChange={(e) => setAgendamentoForm({ ...agendamentoForm, tipoServico: e.target.value })} placeholder="Tipo de serviço" required />
            <input value={agendamentoForm.petId} onChange={(e) => setAgendamentoForm({ ...agendamentoForm, petId: e.target.value })} placeholder="ID do pet" required />
            <button className="primary-btn" type="submit">{agendamentoEditId ? 'Atualizar agendamento' : 'Adicionar agendamento'}</button>
            {agendamentoEditId && <button className="ghost-btn" type="button" onClick={() => { setAgendamentoEditId(''); setAgendamentoForm(agendamentoInicial); }}>Cancelar edição</button>}
          </form>

          {loading ? <p>Carregando...</p> : agendamentos.length === 0 ? <p>Nenhum agendamento encontrado.</p> : (
            <ul className="card-list">
              {agendamentos.map((ag) => (
                <li key={ag.id} className="card-item">
                  <strong>{ag.tipoServico}</strong>
                  <p>Data: {new Date(ag.data).toLocaleString('pt-BR')}</p>
                  <p>Pet ID: {ag.petId}</p>
                  <div className="actions-row">
                    <button className="ghost-btn" onClick={() => selecionarAgendamento(ag)}>Editar</button>
                    <button className="danger-btn" onClick={() => excluirAgendamento(ag.id)}>Excluir</button>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </article>
      </section>
    </div>
  );
}
