import Dashboardstyle from "../../Style/Dashboard/Dashboard.module.css";
import book from "../../assets/image/book.png";
import userpic from "../../assets/image/memberpic.png";
const StudyMonthly = ({ recordData, user, userTime }) => {
  return (
    <div>
      <h3 className={Dashboardstyle.flexrangebox} style={{ fontSize: "22px" }}>
        나의 상태
      </h3>
      <div className={Dashboardstyle.DashMyStatus}>
        <img
          alt="book"
          src={book}
          style={{ width: "30px", marginLeft: "3px" }}
        ></img>
        {!recordData && <p>스터디 시간이 없습니다</p>}
        {recordData && (
          <p>
            총 {parseInt(recordData.totalTime / 3600)}시간{" "}
            {parseInt((recordData.totalTime % 3600) / 60)}분{" "}
            {parseInt(recordData.totalTime % 60)}초 공부했습니다
          </p>
        )}
        {!userTime[0] && <p>최근 스터디가 없습니다</p>}
        <p>최근 스터디는 {userTime[0] && userTime[0].day} 입니다</p>
      </div>
      <div className={Dashboardstyle.DashMyStatus}>
        <img
          alt="userpic"
          src={userpic}
          style={{ width: "30px", marginLeft: "3px", marginTop: "5px" }}
        ></img>
        {user.studies && user.studies.length <= 0 && (
          <p>스터디를 참가해주세요!</p>
        )}
        {user.studies && user.studies.length > 0 && (
          <p>{user.studies.length}개 스터디 참가</p>
        )}
        <p>{user.badges && user.badges.length}개 뱃지 보유</p>
      </div>
    </div>
  );
};

export default StudyMonthly;
