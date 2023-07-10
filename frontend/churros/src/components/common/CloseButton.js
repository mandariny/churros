
import { IoClose } from "react-icons/io5";

const CloseButton = ({ iconSize, className, onClose }) => {
  return (
    <div
      className={`${className} p-1 text-gray-500 rounded-lg hover:bg-red-500 transition duration-300 hover:text-white transition duration-300`}
      onClick={onClose}
    >
      <IoClose size={iconSize} />
    </div>
  );
};

export default CloseButton;
