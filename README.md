# RecipeCommunity - Rest API
Restful API for a Recipe Community, created using Spring Boot.
# About the Project 
This project is a CRUD REST API for a Recipe Community, written in Java and built with Spring Boot.<br />
About Recipe Community:
- users must sign-up to join the community
- authenticated users have access to all recipes added by other users
- users can add, edit and delete their own recipes 
- users can comment on any recipe
- users can save any recipe

# Api Endpoints
### Auth
| Method | Url          | Decription  |
| ------ |------------| ------------|
| POST | /api/auth/sign-in | Log in |
| POST | /api/auth/sign-up | Sign up |
### Users
| Method | Url          | Decription  |
| ------ |------------| ------------|
| GET | /api/users | Get all users |
| GET | /api/users/{id} | Get user by id |
| GET | /api/users/me | Get logged in user |
### Saved Recipes
| Method | Url          | Decription  |
| ------ |------------| ------------|
| GET | /api/users/me/saved-recipes | Get recipes saved by logged in user |
| POST | /api/users/me/saved-recipes | Save a new recipe |
| GET | /api/users/me/saved-recipes/{id} | Get a saved recipe (If the recipe's been saved by logged in user) |
| DELETE | /api/users/me/saved-recipes/{id} | Delete saved recipe (If the recipe's been saved by logged in user) |

### Recipes
| Method | Url          | Decription  |
| ------ |------------| ------------|
| GET  | /api/recipes     | Get all recipes |
| GET  | /api/recipes/{id} | Get a recipe by id |
| GET  | /api/recipes/my | Get recipes created by logged in user |
| POST | /api/recipes/my | Create a new recipe | 
| GET  | /api/recipes/my/{id} | Get a recipe by id (If the recipe's been created by logged in user) |
| DELETE | /api/recipes/my/{id} | Delete a recipe by id (If the recipe's been created by logged in user) |
| PUT  | /api/recipes/my/{id} | Update a recipe (If the recipe's been created by logged in user) |
| GET  | /api/recipes/created-by-{username} | Get recipes created by user with a given username |

### Comments
| Method | Url          | Decription  |
| ------ |------------| ------------|
| GET  | /api/recipes/{recipeId}/comments | Get comments that belong to a recipe with given id |
| POST | /api/recipes/{recipeId}/comments | Create a new comment for recipe with id = recipeId |
| GET  | /api/recipes/{recipeId}/comments/{id} | Get a comment by id (If it belongs to a recipe with id = recipeId) |
| DELETE | /api/recipes/{recipeId}/comments/{id} | Delete a comment by id (If the comment's been created by logged in user) |

# Sample JSON Request Bodys
##### /api/auth/sign-in & /api/auth/sign-up
```
{
    "username": "user1",
    "password": "pass"
}
```
##### /api/users/me/saved-recipes
```
{
    "recipeToBeSavedId": "3"
}
```
##### /api/recipes/my & /api/recipes/my/{id}
```
{
    "title": "Cake",
    "text": "First preheat oven to"
}
```
##### /api/recipes/{recipeId}/comments
```
{
    "text": "Thanks"
}
```
# Technologies
- Java 11
- Spring Boot 2.4.0
- Hibernate 5
- JUnit 5
- JSON Web Tokens
- Spring Security
- MySQL
- JPA
- Spring HATEOAS
- Maven
