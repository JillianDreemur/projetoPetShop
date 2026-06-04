(function () {
  const form = document.getElementById('form-agendamento');
  if (!form) return;

  const checkboxes = form.querySelectorAll('.servico-check');
  const resumoItens = document.getElementById('resumo-itens');
  const resumoTotal = document.getElementById('resumo-total');
  const pagamentoBox = document.getElementById('pagamento-box');

  function selecionados() {
    return Array.from(checkboxes).filter((cb) => cb.checked);
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
  }

  checkboxes.forEach((cb) => cb.addEventListener('change', atualizarResumo));
  atualizarResumo();
})();
