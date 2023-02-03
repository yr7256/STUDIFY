import React from "react";

import Topbar from "../Topbar/Topbar";
import RoungeBanner from "./RoungeBanner";
import StudyRounge from "./StudyRounge";
import RoungeList from "./RoungeList";
import RoungeStyle from "./StudyRounge.module.css";

const StudyRoungeMain = () => {
  return (
    <div className={RoungeStyle.StudyRoungeContainer}>
      <Topbar />

      <h1>스터디 라운지</h1>

      <RoungeBanner />

      <StudyRounge />

      <RoungeList />
    </div>
  );
};

export default StudyRoungeMain;