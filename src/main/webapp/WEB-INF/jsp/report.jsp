<html>

	<head>
		<title>:: Expense Manager ::</title>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.0/jquery.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
	</head>
	
	<body>
	
		<div id="upload">
			<div class="container">
				<font color="green">File <b>${filename} </b> uploaded successfully</font>
			</div>
			<div class="container mt-3">
			  <form action="upload" method="post" enctype="multipart/form-data">
			    <p>Upload csv file:</p>
			    <div class="custom-file mb-3">
			      <input type="file" class="custom-file-input" id="customFile" name="file">
			      <label class="custom-file-label" for="customFile">Choose file</label>
			    </div>
			   
			    <div class="mt-3">
			      <button type="submit" class="btn btn-primary">Submit</button>
			    </div>
			  </form>
			</div>
		</div>
		<br>
		<br>
		<br>
	    <div class="container" id="report">
		  <p>View report</p>
		  <form action="report" method="post">
		    <div class="form-group">
		      <label for="fromDate">From Date:</label>
		      <input type="text" class="form-control" id="fromDate" placeholder="dd-MM-yyyy" name="fromDate">
		    </div>
		    <div class="form-group">
		      <label for="toDate">Password:</label>
		      <input type="text" class="form-control" id="toDate" placeholder="dd-MM-yyyy" name="toDate">
		    </div>
		    
		    <button type="submit" class="btn btn-primary">Report</button>
		  </form>
		</div>
	    
	    <script>
			// Add the following code if you want the name of the file appear on select
			$(".custom-file-input").on("change", function() {
			  var fileName = $(this).val().split("\\").pop();
			  $(this).siblings(".custom-file-label").addClass("selected").html(fileName);
			});
		</script>
	
	</body>
	
</html>