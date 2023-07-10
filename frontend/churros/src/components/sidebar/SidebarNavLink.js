import { NavLink } from "react-router-dom";

const SidebarNavLink = ({
  navigateTo,
  itemId,
  className,
  children,
  onClick,
}) => {
  return (
    <NavLink
      to={navigateTo}
      className={({ isActive }) =>
        `flex flex-row justify-start items-center w-full ${className} ${
          isActive ? "bg-stone-300" : ""
        }`
      }
    >
      {children}
    </NavLink>
  );
};

export default SidebarNavLink;
