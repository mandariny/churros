import React, { useState, useEffect } from "react";

const Carousel = ({ slides }) => {
  const [currentSlide, setCurrentSlide] = useState(0);

  const nextSlide = () => {
    setCurrentSlide((prevSlide) => (prevSlide + 1) % slides.length);
  };

  const prevSlide = () => {
    setCurrentSlide(
      (prevSlide) => (prevSlide - 1 + slides.length) % slides.length
    );
  };

  useEffect(() => {
    const intervalId = setInterval(() => {
      nextSlide();
    }, 5000); // 5초마다 슬라이드 변경

    return () => {
      clearInterval(intervalId);
    };
  }, []);

  return (
    <div className="relative flex w-full h-full overflow-hidden">
      <button
        className="absolute top-1/2 z-10 bg-black bg-opacity-10 text-white text-2xl cursor-pointer p-2 -translate-y-1/2 left-0 h-full w-20"
        onClick={prevSlide}
      >
        &larr;
      </button>
      {slides.map((slide, idx) => (
        <div
          key={idx}
          className={`${
            idx === currentSlide
              ? "relative w-full h-full overflow-hidden transition-transform duration-1000 ease-in-out"
              : "hidden"
          }`}
          style={{
            transform: `translateX(${(idx - currentSlide) * 100}%)`,
          }}
        >
          <img
            src={`${slide.src}`}
            alt={slide.alt}
            className={
              "absolute object-cover w-full h-auto top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2"
            }
          />
        </div>
      ))}
      <button
        className="absolute top-1/2 z-10 bg-black bg-opacity-10 text-white text-2xl cursor-pointer p-2 -translate-y-1/2 right-0 h-full w-20"
        onClick={nextSlide}
      >
        &rarr;
      </button>
    </div>
  );
};

export default Carousel;
