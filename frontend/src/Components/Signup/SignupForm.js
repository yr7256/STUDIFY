import { useState } from "react";
import { useDispatch } from "react-redux";
import { codenumActions } from "../../store/SignupStore";
import axios from "axios";
import swal from "sweetalert";
import ModalSignup from "../UI/ModalSignup";
import logo from "../../assets/image/logo.png";
import SignupStyle from "../../Style/Signup/SignupForm.module.css";

const SignupForm = () => {
  const dispatch = useDispatch();

  //이메일, 비밀번호, 비밀번호 확인
  const [Email, setEmail] = useState("");
  const [Password, setPassword] = useState("");
  const [PasswordCheck, setPasswordCheck] = useState("");
  const [Name, setName] = useState("");
  const [region, setRegion] = useState("");
  const [generation, setGeneration] = useState("");
  const [clssNum, setClassnum] = useState("");

  //오류 메시지 상태저장
  const [emailMessage, setEmailMessage] = useState("");
  const [passwordMessage, setPasswordMessage] = useState("");
  const [passwordCheckMessage, setPasswordCheckMessage] = useState("");

  //유효성 검사
  const [isEmail, setIsEmail] = useState(false);
  const [openModal, setOpenModal] = useState(false);
  const [isPassword, setIsPassword] = useState(false);
  const [isPasswordCheck, setIsPasswordCheck] = useState(false);

  const geneChange = (e) => {
    setGeneration(e.target.value);
  };
  const regionChange = (e) => {
    setRegion(e.target.value);
  };

  const changeClassHandler = (e) => {
    console.log(e.target.value);
    if (e.target.value < 0) {
      swal("잘못된 반 입력입니다. 다시 입력해주세요.");
      setClassnum(0);
    } else if (e.target.value > 20) {
      swal("잘못된 반 입력입니다. 다시 입력해주세요.");
      setClassnum(20);
    } else {
      setClassnum(e.target.value);
    }
  };

  const onNameHandler = (event) => {
    setName(event.currentTarget.value);
  };

  const OnSubmitHandler = async (event) => {
    // const [data, setData] = useState("");
    event.preventDefault();

    try {
      // 끝나면 살리기
      const response = await axios.post("/api/v1/users/auth/mail/register", {
        email: Email,
        password: Password,
        name: Name,
        domain: `${window.location.host}`,
        region: region,
        generation: generation,
        classNum: clssNum,
      });
      setOpenModal(true);
      console.log(response);
      dispatch(codenumActions.changecode(response.code));
    } catch (err) {
      // setOpenModal(true); // 끝나면 삭제
      console.error(err);
      console.log(err.response.data.message);
      swal("중복된 이메일입니다. 다시 입력해주세요.");
    }
  };
  const onChangeEmail = (event) => {
    const emailRegex =
      /([\w-.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
    const emailCurrent = event.target.value;
    setEmail(emailCurrent);
    if (!emailRegex.test(emailCurrent)) {
      setEmailMessage("이메일 형식이 틀렸습니다");
      setIsEmail(false);
    } else {
      setEmailMessage("올바른 이메일 형식입니다");
      setIsEmail(true);
    }
  };
  const onChangePassword = (event) => {
    const passwordRegex =
      /^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,25}$/;
    const passwordCurrent = event.target.value;
    setPassword(passwordCurrent);
    if (!passwordRegex.test(passwordCurrent)) {
      setPasswordMessage(
        "영문자+숫자+특수문자 조합으로 8자리 이상 입력해주세요."
      );
      setIsPassword(false);
    } else {
      setPasswordMessage("안전한 비밀번호입니다.");
      setIsPassword(true);
    }
  };
  const onChangePasswordCheck = (event) => {
    const passwordCheckCurrent = event.target.value;
    setPasswordCheck(passwordCheckCurrent);
    if (Password === passwordCheckCurrent) {
      setPasswordCheckMessage("동일하게 입력되었습니다.");
      setIsPasswordCheck(true);
    } else {
      setPasswordCheckMessage("비밀번호가 다릅니다.");
      setIsPasswordCheck(false);
    }
  };
  return (
    <div className={SignupStyle.signup_background}>
      <form onSubmit={OnSubmitHandler} className={SignupStyle.signupform_div}>
        <img alt="logo" src={logo} className={SignupStyle.signup_logo}></img>
        <div className={SignupStyle.signupinput_div}>
          <label className={SignupStyle.signupform_label}>이메일 </label>
          <div>
            <input
              type="email"
              value={Email}
              onChange={onChangeEmail}
              className={SignupStyle.signup_input}
            ></input>
            <p className={SignupStyle.signup_message}>
              {Email.length > 0 && (
                <span className={`message ${isEmail ? "success" : "error"}`}>
                  {emailMessage}
                </span>
              )}
            </p>
          </div>

          <label className={SignupStyle.signupform_label}>비밀번호</label>
          <div>
            <input
              type="password"
              value={Password}
              onChange={onChangePassword}
              className={SignupStyle.signup_input}
            ></input>
            <p className={SignupStyle.signup_message}>
              {Password.length > 0 && (
                <span className={`message ${isPassword ? "success" : "error"}`}>
                  {passwordMessage}
                </span>
              )}
            </p>
          </div>
          <label className={SignupStyle.signupform_label}>비밀번호 확인</label>
          <div>
            <input
              type="password"
              value={PasswordCheck}
              onChange={onChangePasswordCheck}
              className={SignupStyle.signup_input}
            ></input>
            <p className={SignupStyle.signup_message}>
              {PasswordCheck.length > 0 && (
                <span
                  className={`message ${isPasswordCheck ? "success" : "error"}`}
                >
                  {passwordCheckMessage}
                </span>
              )}
            </p>
          </div>
          <label className={SignupStyle.signupform_label}>이름</label>
          <div>
            <input
              type="name"
              value={Name}
              onChange={onNameHandler}
              className={SignupStyle.signup_input}
            ></input>
          </div>
          <div>
            <label className={SignupStyle.signupform_label}>지역</label>
            <select
              onChange={regionChange}
              className={SignupStyle.ClassSelectbox}
            >
              <option value="">-- 선택하세요 --</option>
              <option key="3" value="서울">
                서울
              </option>
              <option key="4" value="대전">
                대전
              </option>
              <option key="5" value="구미">
                구미
              </option>
              <option key="6" value="부울경">
                부울경
              </option>
              <option key="7" value="광주">
                광주
              </option>
            </select>
          </div>
          <div>
            <label className={SignupStyle.signupform_label}>기수</label>
            <select
              onChange={geneChange}
              className={SignupStyle.ClassSelectbox}
            >
              <option value="">-- 선택하세요 --</option>
              <option key="8기" value="8">
                8기
              </option>
              <option key="9기" value="9">
                9기
              </option>
              <option key="10기" value="10">
                10기 (예정)
              </option>
            </select>
          </div>
          <div className={SignupStyle.signupinput_div}>
            <label className={SignupStyle.signupform_label}>반</label>
            <div>
              <input
                type="number"
                className={SignupStyle.signup_input}
                onChange={changeClassHandler}
                value={clssNum}
              ></input>
            </div>
          </div>
          <br></br>
          <button
            type="submit"
            disabled={!(isEmail && isPassword && isPasswordCheck)}
            className={SignupStyle.signup_button}
          >
            가입하기
          </button>
        </div>
        <ModalSignup
          open={openModal}
          onClose={() => setOpenModal(false)}
          userEmail={Email}
        />
      </form>
    </div>
  );
};

export default SignupForm;
