module.exports = {
  presets: ['module:metro-react-native-babel-preset'],
  overrides: [
    {
      test: /\.tsx?$/,
      presets: ['@babel/preset-typescript'],
    },
  ],
};