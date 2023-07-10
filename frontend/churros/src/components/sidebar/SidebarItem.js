const SidebarItem = ({className, children}) => {
  return (
    <div
      className={`flex flex-row justify-start items-center ${className}`}
    >
      {children}
    </div>
  );
};

export default SidebarItem;
