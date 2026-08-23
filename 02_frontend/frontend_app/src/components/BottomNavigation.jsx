import { HomeIcon, MenuIcon, ReceiptIcon, UserIcon } from './Icons';

const items = [
  { id: 'home', label: 'Início', Icon: HomeIcon },
  { id: 'menu', label: 'Cardápio', Icon: MenuIcon },
  { id: 'orders', label: 'Pedidos', Icon: ReceiptIcon },
  { id: 'profile', label: 'Perfil', Icon: UserIcon }
];

export function BottomNavigation({ activeTab, onNavigate }) {
  return <nav className="bottom-navigation" aria-label="Navegação principal">
    <div>
      {items.map(({ id, label, Icon }) => <button
        key={id}
        className={activeTab === id ? 'is-active' : ''}
        onClick={() => onNavigate(id)}
      >
        <Icon />
        <span>{label}</span>
      </button>)}
    </div>
  </nav>;
}
