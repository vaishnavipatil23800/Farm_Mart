import React, { useState } from "react";
import { FaRegEyeSlash, FaRegEye } from "react-icons/fa6"; // Combine the imports
import toast from "react-hot-toast";
import SummaryApi from "../common/SummaryApi";
import Axios from "../utils/Axios";
import AxiosToastError from "../utils/AxiosToastError";
import { Link, useNavigate } from "react-router-dom"; // Ensure useNavigate is used
import { setUserDetails } from "../store/userSlice";
import fetchUserDetails from "../utils/fetchUserDetails";
import { useDispatch } from "react-redux";

const Login = () => {
  const [data, setData] = useState({
    email: "",
    password: "",
  });
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate(); // Navigation hook
  const dispatch = useDispatch();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setData((prev) => ({ ...prev, [name]: value }));
  };

  const isValid = Object.values(data).every((el) => el);

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await Axios({
        ...SummaryApi.login,
        data: data,
      });

      if (response.data.error) {
        toast.error(response.data.message);
      }

      if (response.data.success) {
        toast.success(response.data.message);
        localStorage.setItem("accesstoken", response.data.data.accesstoken);

        const userDetails = await fetchUserDetails();
        dispatch(setUserDetails(userDetails.data));

        setData({ email: "", password: "" });

        navigate("/"); // Redirect after login
      }
    } catch (error) {
      AxiosToastError(error); // Handle API errors
    }
  };

  return (
    <section
      className="w-full h-[81vh] flex flex-col justify-center items-center bg-cover bg-no-repeat bg-center"
      style={{ backgroundImage: "url('/src/assets/LoginBG.jpg')" }}>
        
      <div className="bg-white w-full max-w-lg mx-auto rounded-lg p-8 shadow-lg">
        <h2 className="text-2xl font-bold text-center mb-6">Login</h2>

        <form className="grid gap-4" onSubmit={handleSubmit}>
          {/* Email Field */}
          <div className="grid gap-1">
            <label htmlFor="email">Email:</label>
            <input
              type="email"
              id="email"
              className="bg-gray-100 p-2 border rounded outline-none focus:border-green-500"
              name="email"
              value={data.email}
              onChange={handleChange}
              placeholder="Enter your email"
              required
            />
          </div>

          {/* Password Field */}
          <div className="grid gap-1">
            <label htmlFor="password">Password:</label>
            <div className="bg-gray-100 p-2 border rounded flex items-center focus-within:border-green-500">
              <input
                type={showPassword ? "text" : "password"}
                id="password"
                className="w-full outline-none bg-transparent"
                name="password"
                value={data.password}
                onChange={handleChange}
                placeholder="Enter your password"
                required
              />
              <div
                onClick={() => setShowPassword((prev) => !prev)}
                className="cursor-pointer"
              >
                {showPassword ? <FaRegEye /> : <FaRegEyeSlash />}
              </div>
            </div>
            <Link
              to="/forgot-password"
              className="text-sm text-green-600 hover:underline ml-auto"
            >
              Forgot password?
            </Link>
          </div>

          {/* Login Button */}
          <button
            disabled={!isValid}
            className={`w-full py-2 rounded font-semibold tracking-wide ${
              isValid
                ? "bg-green-700 hover:bg-green-600 text-white"
                : "bg-gray-400 cursor-not-allowed"
            }`}
          >
            Login
          </button>
        </form>

        {/* Register Link */}
        <p className="text-center mt-4">
          Don't have an account?{" "}
          <Link
            to="/register"
            className="text-green-700 font-semibold hover:underline"
          >
            Register
          </Link>
        </p>
      </div>
    </section>
  );
};

export default Login;
