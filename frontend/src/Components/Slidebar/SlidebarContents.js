import sidestyle from "../../Style/Slidebar/Slidebar.module.css";
import DashboardProfile from "../DashBoard/DashboardProfile";

const SlidebarContents = () => {
  return (
    <div className={sidestyle.slidecontent}>
      <div>
        <DashboardProfile />
      </div>
    </div>
  );
};

export default SlidebarContents;
