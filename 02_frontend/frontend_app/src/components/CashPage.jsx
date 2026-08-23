import { useEffect, useMemo, useState } from 'react';
import { ArrowLeftIcon, SearchIcon } from './Icons';

const money = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
const statusLabel = { OPEN: 'Aberta', CLOSED: 'Finalizada', CANCELLED: 'Cancelada' };

function CashModal({ title, onClose, children }) {
  return <div className="cash-modal-backdrop" onMouseDown={onClose}><section className="cash-modal" onMouseDown={event => event.stopPropagation()}><button className="cash-modal__close" onClick={onClose}>×</button><p>CAIXA JACK</p><h2>{title}</h2>{children}</section></div>;
}

function NewTabModal({ api, onClose, onCreated }) {
  const [saving, setSaving] = useState(false);
  async function submit(event) {
    event.preventDefault(); setSaving(true);
    const form = new FormData(event.currentTarget);
    try { onCreated(await api('/api/cash/tabs', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(Object.fromEntries(form)) })); }
    catch (error) { window.alert(error.message); setSaving(false); }
  }
  return <CashModal title="Nova comanda" onClose={onClose}><form className="cash-form" onSubmit={submit}><div className="cash-form__grid"><label>Número da comanda<input name="code" placeholder="Gerado automaticamente" /></label><label>Tipo de atendimento<select name="serviceType" defaultValue="TABLE"><option value="TABLE">Presencial</option><option value="PICKUP">Retirada</option><option value="DELIVERY">Delivery</option></select></label></div><div className="cash-form__grid"><label>Nome do cliente<input name="customerName" placeholder="Opcional" /></label><label>Telefone<input name="phone" placeholder="Opcional" /></label></div><label>Observações<textarea name="notes" placeholder="Informações para o atendimento" /></label><button className="cash-primary" disabled={saving}>{saving ? 'Criando...' : 'Criar comanda'}</button></form></CashModal>;
}

function AddProductModal({ product, api, tabId, onClose, onSaved }) {
  const [quantity, setQuantity] = useState(1);
  const [extras, setExtras] = useState([]);
  const [notes, setNotes] = useState('');
  const total = (product.price + extras.reduce((sum, name) => sum + Number(product.addons.find(item => item.name === name)?.price || 0), 0)) * quantity;
  async function add() {
    try { onSaved(await api(`/api/cash/tabs/${tabId}/items`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ productId: product.id, quantity, extras, notes }) })); }
    catch (error) { window.alert(error.message); }
  }
  const toggle = name => setExtras(values => values.includes(name) ? values.filter(item => item !== name) : [...values, name]);
  return <CashModal title={product.name} onClose={onClose}><img className="cash-product-modal__image" src={product.image} alt={product.name} /><p className="cash-product-modal__copy">{product.description}</p><div className="cash-quantity"><button onClick={() => setQuantity(value => Math.max(1, value - 1))}>−</button><strong>{quantity}</strong><button onClick={() => setQuantity(value => value + 1)}>+</button></div>{product.addons.length > 0 && <fieldset className="cash-addons"><legend>Adicionais</legend>{product.addons.map(addon => <label key={addon.name}><span><input type="checkbox" checked={extras.includes(addon.name)} onChange={() => toggle(addon.name)} /> {addon.name}</span><strong>+ {money.format(addon.price)}</strong></label>)}</fieldset>}<label>Observação<textarea value={notes} onChange={event => setNotes(event.target.value)} placeholder="Ex.: sem cebola" /></label><button className="cash-primary" onClick={add}>Adicionar — {money.format(total)}</button></CashModal>;
}

export function CashPage({ api, onBack, onLogout, user }) {
  const [tabs, setTabs] = useState([]);
  const [products, setProducts] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [query, setQuery] = useState('');
  const [status, setStatus] = useState('OPEN');
  const [productQuery, setProductQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [modal, setModal] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState('PIX');

  async function load(silent = false) {
    if (!silent) setLoading(true);
    try { const [tabData, productData] = await Promise.all([api('/api/cash/tabs'), api('/api/cash/products')]); setTabs(tabData); setProducts(productData); setError(''); setSelectedId(current => current ?? tabData.find(tab => tab.status === 'OPEN')?.id ?? tabData[0]?.id ?? null); }
    catch (loadError) { setError(loadError.message); }
    finally { setLoading(false); }
  }
  useEffect(() => { load(); const interval = window.setInterval(() => load(true), 10000); return () => window.clearInterval(interval); }, []);
  const filteredTabs = useMemo(() => tabs.filter(tab => (status === 'ALL' || tab.status === status) && (!query.trim() || `${tab.code} ${tab.customerName || ''}`.toLowerCase().includes(query.toLowerCase()))), [tabs, query, status]);
  const selected = tabs.find(tab => tab.id === selectedId);
  const filteredProducts = products.filter(product => !productQuery.trim() || `${product.name} ${product.bread}`.toLowerCase().includes(productQuery.toLowerCase()));
  const replaceTab = tab => { setTabs(current => current.map(item => item.id === tab.id ? tab : item)); setSelectedId(tab.id); setModal(null); };
  async function action(path, options) { try { replaceTab(await api(path, options)); } catch (actionError) { window.alert(actionError.message); } }
  async function quantity(item, amount) { const quantity = item.quantity + amount; if (quantity < 1) return; await action(`/api/cash/tabs/${selected.id}/items/${item.id}`, { method: 'PATCH', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ quantity, notes: item.notes }) }); }
  async function cancelItem(item) { const reason = window.prompt(`Motivo do cancelamento de ${item.productName}:`); if (!reason) return; await action(`/api/cash/tabs/${selected.id}/items/${item.id}/cancel`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ reason }) }); }
  async function toggleAvailability(product) { const reason = product.available ? window.prompt('Motivo da indisponibilidade:', 'Produto esgotado') : null; if (product.available && !reason) return; try { await api(`/api/cash/products/${product.id}/availability`, { method: 'PATCH', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ available: !product.available, reason }) }); await load(true); } catch (actionError) { window.alert(actionError.message); } }
  async function cancelTab() { if (!window.confirm('Tem certeza que deseja cancelar esta comanda?')) return; const reason = window.prompt('Informe o motivo do cancelamento:'); if (!reason) return; await action(`/api/cash/tabs/${selected.id}/cancel`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ reason }) }); }
  async function deleteEmptyTab() { if (!window.confirm('Excluir esta comanda vazia? Esta ação não poderá ser desfeita.')) return; try { await api(`/api/cash/tabs/${selected.id}`, { method: 'DELETE' }); setTabs(current => current.filter(tab => tab.id !== selected.id)); setSelectedId(null); } catch (actionError) { window.alert(actionError.message); } }
  async function finalize() { if (!window.confirm(`Finalizar ${selected.code} no valor de ${money.format(selected.total)}?`)) return; await action(`/api/cash/tabs/${selected.id}/finalize`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ paymentMethod }) }); }

  return <section className="cash-page">
    <header className="cash-header"><button className="cash-back" onClick={onBack}><ArrowLeftIcon /> Início</button><div><span>MÓDULO OPERACIONAL</span><h1>Caixa & Comandas</h1><p>Atendimento presencial integrado ao cardápio Jack.</p></div><div className="cash-header__actions"><span>{user?.username}</span><button className="cash-logout" onClick={onLogout}>Sair</button><button className="cash-new" onClick={() => setModal('new')}>+ Nova comanda</button></div></header>
    <div className="cash-stats"><article><span>Comandas abertas</span><strong>{tabs.filter(tab => tab.status === 'OPEN').length}</strong></article><article><span>Em aberto</span><strong>{money.format(tabs.filter(tab => tab.status === 'OPEN').reduce((sum, tab) => sum + Number(tab.total), 0))}</strong></article><article><span>Produtos esgotados</span><strong>{products.filter(product => !product.available).length}</strong></article></div>
    {error && <div className="cash-feedback is-error">{error} <button onClick={() => load()}>Tentar novamente</button></div>}
    <main className="cash-layout">
      <aside className="cash-tabs"><div className="cash-panel-title"><div><span>ATENDIMENTO</span><h2>Comandas</h2></div><button onClick={() => load()}>↻</button></div><label className="cash-search"><SearchIcon /><input value={query} onChange={event => setQuery(event.target.value)} placeholder="Número ou cliente" /></label><div className="cash-filters">{[['OPEN','Abertas'],['CLOSED','Histórico'],['CANCELLED','Canceladas'],['ALL','Todas']].map(([value,label]) => <button className={status === value ? 'is-active' : ''} onClick={() => setStatus(value)} key={value}>{label}</button>)}</div><div className="cash-tab-list">{loading ? <p className="cash-empty">Carregando comandas...</p> : filteredTabs.length ? filteredTabs.map(tab => <button className={`cash-tab-card ${selectedId === tab.id ? 'is-selected' : ''}`} onClick={() => setSelectedId(tab.id)} key={tab.id}><div><strong>{tab.code}</strong><span className={`cash-status cash-status--${tab.status.toLowerCase()}`}>{statusLabel[tab.status]}</span></div><p>{tab.customerName || 'Cliente presencial'}</p><small>{tab.itemCount} itens • {new Date(tab.openedAt).toLocaleTimeString('pt-BR', { hour:'2-digit', minute:'2-digit' })}</small><b>{money.format(tab.total)}</b></button>) : <p className="cash-empty">Nenhuma comanda encontrada.</p>}</div></aside>
      <section className="cash-detail">{selected ? <><div className="cash-detail__head"><div><span>COMANDA</span><h2>{selected.code}</h2><p>{selected.customerName || 'Cliente presencial'} • {statusLabel[selected.status]}</p></div>{selected.status === 'OPEN' && <div>{selected.items.length === 0 && <button className="cash-danger-link" onClick={deleteEmptyTab}>Excluir</button>}<button className="cash-danger-link" onClick={cancelTab}>Cancelar comanda</button></div>}</div><div className="cash-items">{selected.items.length ? selected.items.map(item => <article className={item.status === 'CANCELLED' ? 'is-cancelled' : ''} key={item.id}><div><strong>{item.quantity}× {item.productName}</strong>{item.extras && <p>+ {item.extras}</p>}{item.removals && <p>Sem {item.removals}</p>}{item.notes && <p>{item.notes}</p>}</div><b>{money.format(item.total)}</b>{selected.status === 'OPEN' && item.status === 'ACTIVE' && <div className="cash-item-actions"><button onClick={() => quantity(item,-1)}>−</button><button onClick={() => quantity(item,1)}>+</button><button className="is-danger" onClick={() => cancelItem(item)}>Cancelar</button></div>}</article>) : <p className="cash-empty">Nenhum item lançado nesta comanda.</p>}</div><footer className="cash-total"><div><span>Subtotal</span><strong>{money.format(selected.subtotal)}</strong></div><div className="is-total"><span>Total</span><strong>{money.format(selected.total)}</strong></div>{selected.status === 'OPEN' && <><label>Forma de pagamento<select value={paymentMethod} onChange={event => setPaymentMethod(event.target.value)}><option value="CASH">Dinheiro</option><option value="PIX">PIX</option><option value="DEBIT_CARD">Cartão de débito</option><option value="CREDIT_CARD">Cartão de crédito</option></select></label><button className="cash-primary" disabled={!selected.itemCount} onClick={finalize}>Finalizar comanda</button></>}</footer></> : <p className="cash-empty cash-empty--large">Selecione ou crie uma comanda para começar.</p>}</section>
      <aside className="cash-catalog"><div className="cash-panel-title"><div><span>LANÇAMENTO</span><h2>Produtos</h2></div></div><label className="cash-search"><SearchIcon /><input value={productQuery} onChange={event => setProductQuery(event.target.value)} placeholder="Buscar produto" /></label><div className="cash-product-list">{filteredProducts.map(product => <article className={!product.available ? 'is-sold-out' : ''} key={product.id}><img src={product.image} alt="" /><div><strong>{product.name}</strong><span>{money.format(product.price)}</span>{!product.available && <small>× {product.unavailableReason || 'Produto esgotado'}</small>}</div><button disabled={!product.available || selected?.status !== 'OPEN'} onClick={() => setModal(product)}>+</button><button className="cash-stock-toggle" onClick={() => toggleAvailability(product)}>{product.available ? 'Esgotar' : 'Disponibilizar'}</button></article>)}</div></aside>
    </main>
    {modal === 'new' && <NewTabModal api={api} onClose={() => setModal(null)} onCreated={tab => { setTabs(current => [tab, ...current]); setSelectedId(tab.id); setStatus('OPEN'); setModal(null); }} />}
    {modal?.id && selected && <AddProductModal product={modal} api={api} tabId={selected.id} onClose={() => setModal(null)} onSaved={replaceTab} />}
  </section>;
}
