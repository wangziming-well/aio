/**
 * Creating a sidebar enables you to:
 - create an ordered group of docs
 - render a sidebar for each doc of that group
 - provide next/previous navigation

 The sidebars can be generated from the filesystem, or explicitly defined here.

 Create as many sidebars as you want.
 */

// @ts-check

/** @type {import('@docusaurus/plugin-content-docs').SidebarsConfig} */
const sidebars = {
  // By default, Docusaurus generates a sidebar from the docs folder structure
  Sidebar1: [{type: 'autogenerated', dirName: '1_计算机基础'}],
  Sidebar2: [{type: 'autogenerated', dirName: '2_Java'}],
  Sidebar3: [{type: 'autogenerated', dirName: '3_前端'}],
  Sidebar4: [{type: 'autogenerated', dirName: '4_数据库'}],
  Sidebar5: [{type: 'autogenerated', dirName: '5_中间件'}],
  Sidebar6: [{type: 'autogenerated', dirName: '6_开发工具'}],



  // But you can create a sidebar manually
  /*
  tutorialSidebar: [
    'intro',
    'hello',
    {
      type: 'category',
      label: 'Tutorial',
      items: ['tutorial-basics/create-a-document'],
    },
  ],
   */
};

export default sidebars;
