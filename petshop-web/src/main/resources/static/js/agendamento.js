(function () {
  const form = document.getElementById('form-agendamento');
  if (!form) return;

  const checkboxes = form.querySelectorAll('.servico-check');
  const resumoItens = document.getElementById('resumo-itens');
  const resumoTotal = document.getElementById('resumo-total');
  const pagamentoBox = document.getElementById('pagamento-box');
  const dataInput = document.getElementById('ag-data');
  const avisoVaga = document.getElementById('aviso-vaga');
  const btnPagamento = document.getElementById('btn-pagamento');
  const ocupacao = window.OCUPACAO_BANHO_TOSA || {};
  const limite = window.LIMITE_BANHO_TOSA || 3;
  const GRUPO_BANHO_TOSA = 'banho_tosa';

  function selecionados() {
    return Array.from(checkboxes).filter((cb) => cb.checked);
  }

  function temBanhoTosaSelecionado() {
    return selecionados().some((cb) => cb.dataset.grupo === GRUPO_BANHO_TOSA);
  }

  function formatarDataBr(iso) {
    const p = iso.split('-');
    if (p.length !== 3) return iso;
    return p[2] + '/' + p[1] + '/' + p[0];
  }

  function proximaDataComVaga(aPartirIso) {
    const inicio = new Date(aPartirIso + 'T12:00:00');
    for (let i = 0; i < 366; i++) {
      const iso = inicio.toISOString().slice(0, 10);
      if ((ocupacao[iso] || 0) < limite) {
        return iso;
      }
      inicio.setDate(inicio.getDate() + 1);
    }
    return null;
  }

  function diaSeguinte(iso) {
    const d = new Date(iso + 'T12:00:00');
    d.setDate(d.getDate() + 1);
    return d.toISOString().slice(0, 10);
  }

  function verificarVagaData() {
    if (!dataInput || !avisoVaga) return;

    const data = dataInput.value;
    const qtd = data ? (ocupacao[data] || 0) : 0;
    const cheio = data && temBanhoTosaSelecionado() && qtd >= limite;

    if (cheio) {
      let msg = 'Este dia já está cheio para banho/tosa (máximo ' + limite + ' por dia).';
      const sugestao = proximaDataComVaga(diaSeguinte(data));
      if (sugestao) {
        msg += ' Próxima data com vaga: ' + formatarDataBr(sugestao) + '.';
      }
      avisoVaga.textContent = msg;
      avisoVaga.hidden = false;
      if (btnPagamento) btnPagamento.disabled = true;
    } else {
      avisoVaga.textContent = '';
      avisoVaga.hidden = true;
      if (btnPagamento && !form.querySelector('#ag-pet[disabled]')) {
        btnPagamento.disabled = false;
      }
    }
  }

  function atualizarExclusao() {
    const marcados = selecionados();
    const grupos = {};

    marcados.forEach((cb) => {
      const grupo = cb.dataset.grupo;
      if (!grupo) return;
      if (!grupos[grupo]) grupos[grupo] = { pacote: false, item: false };
      if (cb.dataset.pacote === 'true') grupos[grupo].pacote = true;
      else grupos[grupo].item = true;
    });

    checkboxes.forEach((cb) => {
      cb.disabled = false;
      const grupo = cb.dataset.grupo;
      if (!grupo || !grupos[grupo]) return;

      const g = grupos[grupo];
      const isPacote = cb.dataset.pacote === 'true';
      if (g.pacote && !isPacote) cb.disabled = true;
      if (g.item && isPacote) cb.disabled = true;
      if (g.pacote && g.item) {
        if (isPacote) cb.disabled = false;
        else cb.disabled = true;
      }
    });

    marcados.forEach((cb) => {
      if (cb.disabled) cb.checked = false;
    });
  }

  function atualizarResumo() {
    atualizarExclusao();
    const itens = selecionados();
    if (!resumoItens || !resumoTotal) return;

    if (itens.length === 0) {
      resumoItens.innerHTML = '<li>Nenhum serviço selecionado</li>';
      resumoTotal.textContent = 'R$ 0,00';
      if (pagamentoBox) pagamentoBox.hidden = true;
      verificarVagaData();
      return;
    }

    let total = 0;
    resumoItens.innerHTML = '';
    itens.forEach((cb) => {
      const valor = parseFloat(cb.dataset.valor || '0');
      total += valor;
      const li = document.createElement('li');
      li.innerHTML = '<span>' + cb.dataset.nome + '</span><strong>' + cb.dataset.preco + '</strong>';
      resumoItens.appendChild(li);
    });

    resumoTotal.textContent = total.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    if (pagamentoBox) pagamentoBox.hidden = false;
    verificarVagaData();
  }

  checkboxes.forEach((cb) => cb.addEventListener('change', atualizarResumo));
  if (dataInput) dataInput.addEventListener('change', verificarVagaData);
  atualizarResumo();
})();
