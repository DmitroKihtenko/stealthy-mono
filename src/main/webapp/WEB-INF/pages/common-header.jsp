<%@ taglib prefix="c" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.1.3/dist/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
    <link href="<c:url value="static/css/font-awesome.min.css"/>" rel="stylesheet">
    <link href="<c:url value="static/css/backgrounds.css"/>" rel="stylesheet">
    <title>Common header</title>
</head>
<body class="d-flex flex-column mh-100">
<header class="text-center bg-diluted">
    <div class="container">
        <div class="row d-flex">
            <div class="col-12">
                <h2>Stealthy</h2>
            </div>
            <div class="col-6 px-0 py-1 d-flex">
                <sec:authorize access="isAuthenticated()">
                    <div class="btn-group dropright w-25 mr-auto">
                        <button class="btn btn-block btn-sm dropdown-toggle btn-outline-main text-truncate mx-1 my-1" id="userMenu" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <i class="fa fa-user-circle"></i> <sec:authentication property="principal.username" />
                        </button>
                        <div class="dropdown-menu bg-opposite" aria-labelledby="userMenu">
                            <a class="dropdown-item btn-opposite" href="sign-out">
                                <i class="fa fa-sign-out"></i> Logout
                            </a>
                            <a class="dropdown-item btn-opposite" href="space">
                                <i class="fa fa-laptop"></i> Your space
                            </a>
                        </div>
                    </div>
                </sec:authorize>
            </div>
            <div class="col-2 px-1 py-1 d-flex align-items-center">
                <a class="btn btn-block btn-sm btn-outline-main text-truncate" href="sign-up">
                    Sign-up
                </a>
            </div>
            <div class="col-2 px-1 py-1 d-flex align-items-center">
                <a class="btn btn-block btn-sm btn-outline-main text-truncate" href="sign-in">
                    Sign-in
                </a>
            </div>
            <div class="col-2 px-1 py-1 d-flex align-items-center">
                <a class="btn btn-block btn-sm btn-outline-main text-truncate" href="download">
                    Download
                </a>
            </div>
        </div>
    </div>
</header>
<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"
        integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"
        crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"
        integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
        crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"
        integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
        crossorigin="anonymous"></script>
</body>
