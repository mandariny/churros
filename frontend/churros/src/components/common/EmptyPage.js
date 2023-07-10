import React from "react";
import { GiKnot } from "react-icons/gi";

const EmptyPage = ({ message, className }) => {
  return (
    <div
      className={`flex flex-col justify-center items-center h-full w-full text-gray-500`}
    >
      <GiKnot className="text-6xl text-[#CBA870] m-4"/>
      <h2 className="text-lg font-medium text-center">{message}</h2>
    </div>
  );
};

export default EmptyPage;
