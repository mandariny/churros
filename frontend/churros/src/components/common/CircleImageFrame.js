const CircleImageFrame = ({ imageUrl }) => {
  return (
    <div className="flex items-center justify-center mx-2">
      <div className="w-14 h-14 rounded-full border-2 border-white overflow-hidden">
        <img
          src={imageUrl}
          alt="User Profile"
          className="w-full h-full object-cover"
        />
      </div>
    </div>
  );
};

export default CircleImageFrame;
