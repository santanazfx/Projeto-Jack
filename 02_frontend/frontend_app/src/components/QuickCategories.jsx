const quickCategories = [
  { label: 'Pão de hambúrguer', shortLabel: 'Hambúrguer', icon: '🍔', width: '145px' },
  { label: 'Pão francês mata fome', shortLabel: 'Mata fome', icon: '🥖', width: '125px' },
  { label: 'Pão brioche', shortLabel: 'Brioche', icon: '🍞', width: '104px' }
];

export function QuickCategories({ activeCategory, onSelect, onViewAll }) {
  return <section className="quick-categories" aria-labelledby="quick-categories-title">
    <div className="section-row">
      <h2 id="quick-categories-title">Tá com fome?</h2>
      <button className="view-all-button" onClick={onViewAll}>Ver tudo</button>
    </div>
    <p className="quick-categories__subtitle">Escolha seus favoritos e monte seu pedido.</p>
    <div className="quick-categories__row" role="tablist" aria-label="Categorias do cardápio">
      {quickCategories.map(item => <button
        key={item.label}
        role="tab"
        aria-selected={activeCategory === item.label}
        className={activeCategory === item.label ? 'is-selected' : ''}
        style={{ '--category-width': item.width }}
        onClick={() => onSelect(item.label)}
      >
        <span aria-hidden="true">{item.icon}</span>{item.shortLabel}
      </button>)}
    </div>
  </section>;
}
