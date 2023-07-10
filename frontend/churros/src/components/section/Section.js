const Section = ({ children }) => {
  return (
    <section className="flex flex-col justify-start w-full h-full overflow-y-auto">
      {children}
    </section>
  );
};

export default Section;
