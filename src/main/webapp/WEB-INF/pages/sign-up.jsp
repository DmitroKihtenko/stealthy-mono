<%@ page import="top.secret.pojo.schemas.UserRequest" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html class="h-100">
<head>
    <title>Sign-up</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.1.3/dist/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
    <link href="<s:url value="static/css/backgrounds.css"/>" rel="stylesheet">
    <link href="<s:url value="static/css/device-sizes.css"/>" rel="stylesheet">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
</head>
<body class="d-flex flex-column h-100">
<jsp:include page="common-header.jsp" />
<main class="flex-grow-1 bg-main text-center h-100">
    <div class="container h-100">
        <div class="row h-100 d-flex justify-content-md-center align-items-center">
            <div class="col-8 d-flex justify-content-md-center align-items-center bg-diluted text-lg mx-2 my-2">
                <div class="col">
                    <div class="row justify-content-md-center">
                        <div class="col">
                            <h3 class="my-1 w-100 text-center text-truncate">Sign-up an account</h3>
                        </div>
                    </div>
                    <form method="POST">
                        <div class="row">
                            <div class="col my-1">
                                <div class="input-group mb-2 input-group-lg">
                                    <div class="input-group-prepend">
                                        <div class="input-group-text bg-opposite ml-1 my-1">
                                            <i class="fa fa-user"></i>
                                        </div>
                                    </div>
                                    <input id="username-input" type="text" name="username" class="form-control bg-opposite mr-1 my-1" placeholder="Your username"
                                            <% Object userRequest = request.getAttribute("request");
                                            if (userRequest != null) { %>
                                            value="<%= ((UserRequest)userRequest).getUsername() %>"
                                            <% } %>
                                    />
                                </div>
                                <div class="input-group mb-2 input-group-lg"><div class="input-group-prepend">
                                    <div class="input-group-text bg-opposite ml-1 my-1">
                                        <i class="fa fa-lock"></i>
                                    </div>
                                </div>
                                    <input id="password-input" type="password" name="password" class="form-control bg-opposite mr-1 my-1" placeholder="Your password"
                                        <% if (userRequest != null) { %>
                                        value="<%= ((UserRequest)userRequest).getPassword() %>"
                                        <% } %>
                                    />
                                </div>
                            </div>
                        </div>
                        <% Object errors = request.getAttribute("error");
                            if (errors != null) { %>
                        <div class="row">
                            <div class="col my-1">
                                <div class="alert alert-danger" role="alert">
                                    <strong class="text-wrap">
                                        <% for (String message : (Iterable<? extends String>) errors) { %>
                                            <%= message %><br/>
                                        <% } %>
                                    </strong>
                                </div>
                            </div>
                        </div>
                        <% } %>
                        <div class="row justify-content-md-center">
                            <div class="col my-1">
                                <button type="submit" class="w-100 btn btn-diluted btn-lg">
                                    <i class="fa fa-floppy-o"></i> Register
                                </button>
                            </div>
                        </div>
                    </form>
                    <div class="row justify-content-md-center">
                        <div class="col my-1">
                            Already registered? Go to sign-in page
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>
<jsp:include page="common-footer.jsp" />
</body>
</html>
