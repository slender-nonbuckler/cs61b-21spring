# Overview

This is temporarily a placeholder to demonstrate that each project comes with its own README file which is usually
written in [Markdown](https://github.github.com/gfm/#introduction).

Take a look at other projects to learn what contents to put in this file.

# Git Branching Model

Here is a good [article](https://nvie.com/posts/a-successful-git-branching-model/) about branching in a team, and we'll 
apply the development model in our project.

![git branching model](https://nvie.com/img/git-model@2x.png)

We will have four branches **main**, **dev**, **feat** and **fix**. **main** and **dev** are two main branches.
Whenever **dev** is stable, we do a
[pull request](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/about-pull-requests)
to the **main** branch. **feat** means new features and they are assigned to each team member respectively. Whenever a
feature is done, we do a pull request to the **dev** branch. For each bug, we create its own branch based on the
related **feat** branch. For every pull request, usually we will have a senior engineer or even team manager to approve,
meaning that our code is ready to merge into the target branch.