# csc326-TP-212-3

# Extra Credit


# Additional User Role

Code for additional user role: 

Manager: https://github.ncsu.edu/engr-csc326-spring2024/csc326-TP-212-3/blob/57fca01224f447de921b576c78b9db0b6ea7a1df/CoffeeMaker-Individual/src/main/java/edu/ncsu/csc/CoffeeMaker/models/Manager.java

Barista: https://github.ncsu.edu/engr-csc326-spring2024/csc326-TP-212-3/blob/57fca01224f447de921b576c78b9db0b6ea7a1df/CoffeeMaker-Individual/src/main/java/edu/ncsu/csc/CoffeeMaker/models/Barista.java

For our additional user role, we split the Staff Role into two: A barista and a manager. The manager can add ingredients, update the inventory, and view a detailed report of order history. A Barista can add recipes, edit recipes, and fulfill customer orders. This split intends to give CoffeeMaker users more control over their staff and users and make sure that standard user accounts are not accessing things they shouldn't

# Privacy Policy

Our Privacy Policy for CoffeeMaker can be found on its wiki page. The Service Provider of this app is our team (TP-212-3) and the contact provided is our group leader John S. The policy is extensive and covers all Better Business Bureau privacy policy standards. 

# Order History

Code for Order History HTML: https://github.ncsu.edu/engr-csc326-spring2024/csc326-TP-212-3/blob/57fca01224f447de921b576c78b9db0b6ea7a1df/CoffeeMaker-Individual/src/main/resources/templates/orderhistory.html


Wireframe for Order History:
![](https://github.ncsu.edu/engr-csc326-spring2024/csc326-TP-212-3/blob/iwnowell/design/CoffeeMaker-Individual/Images/Order%20history%20wireframe.png)

The order history will be available to see by the Manager user role. It displays all orders with status CLOSED. It summarizes the data from those recipes - the profit made and the ingredients used. This was achieved by adding a statusTimestamp field on each Order. This would track when the current status took effect. As such, when viewing all orders with the status ORDERCLOSED, we are then able to sort chronologically. While sorting, the orders are split into "Today's Orders", "This Month's orders", and "This Year's Orders" which provides a high level overview of how business is going.

![](https://github.ncsu.edu/engr-csc326-spring2024/csc326-TP-212-3/blob/final_bug_fixes/CoffeeMaker-Individual/Images/orderhistory.png)
This photo of the actual implementation shows the graphical view of a few orders placed.

# UI Enhancements

Code Examples:
https://github.ncsu.edu/engr-csc326-spring2024/csc326-TP-212-3/blob/57fca01224f447de921b576c78b9db0b6ea7a1df/CoffeeMaker-Individual/src/main/resources/templates/recipe.html
https://github.ncsu.edu/engr-csc326-spring2024/csc326-TP-212-3/blob/57fca01224f447de921b576c78b9db0b6ea7a1df/CoffeeMaker-Individual/src/main/resources/templates/inventory.html
https://github.ncsu.edu/engr-csc326-spring2024/csc326-TP-212-3/blob/57fca01224f447de921b576c78b9db0b6ea7a1df/CoffeeMaker-Individual/src/main/resources/templates/orderPickup.html
https://github.ncsu.edu/engr-csc326-spring2024/csc326-TP-212-3/blob/57fca01224f447de921b576c78b9db0b6ea7a1df/CoffeeMaker-Individual/src/main/resources/templates/order.html


- Uniform Design

  Throughout the various pages, all share the same color palette and style, characterized by a black border. This consistency ensures simplicity and uniformity in style.

- Color Choices

  To enhance the visual appeal of our page, we have selected three main colors that are distinctive and eye-catching to customers.

- Text Size

  We have implemented animated text across each page's features. The text size is intentionally large to accommodate individuals who face difficulties with small print.

- Navigation Element

  A navigation bar is prominently placed at the top of each page, directing users to their desired sections. This intuitive placement helps users easily understand where to look and click when they wish to navigate to other pages.

- Consolidation of Content

  By integrating different recipes' HTML into a single page, we have centralized control over recipe data. This approach simplifies user interactions and reduces the amount of web resources needed, enhancing both performance and user experience.

