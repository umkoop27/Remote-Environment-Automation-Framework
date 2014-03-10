
/**************************************************************************************************
 *                                                                                                *
 *                    ALL PAGES                                                                   *
 *                                                                                                *
 **************************************************************************************************/

// Configurable Variables
 
var server_mode = true; // set to false if server is not present (for testing only)
var add_device_countdown = 30; // seconds

// Non-Configurable Variables

var base_url = "https://localhost/";
if (!server_mode) base_url = "";

function http_post_request(str_request, async, misc)
{
	var op = (str_request.split("|"))[0];
	var xhr = new XMLHttpRequest();
	
	// server_mode == false
	var index;
	var half_time_ms;
	
	if (!server_mode) // essentially skips HTTP communications (for testing only)
	{
		switch (op)
		{
			case "message_board":
				update_message_board("[10:00] light1 turned on|[10:26] light2 turned on|[11:01] light1 turned off");
				break;
			case "device_list":
				if (misc == "addEditProgram")
					display_devices("light1|light2|thermometer");
				else
					update_list(true, "light1|light2|thermometer", misc) // makes use of a dummy list
				break;
			case "device_info":
				if (misc == "addEditProgram")
					update_dev(xml_timer, (str_request.split("|"))[1]);
				else
					load_device_info(xml_timer);
				break;
			case "service_list":
				update_list(false, "lights|shades|heating", misc) // makes use of a dummy list
				break;
			case "service_info":
				popup_view_open("lights|t`thermometer`Reach Temperature`20|a`light1`Turn On");
				break;
			case "service_representation":
				update_prog_lists("::(::(::(d1:::c1:::a1::)+::(d2:::c2::)::)&::(d3:::c3:::a3::)::)::->d4:::o4:::a4", misc);
				break;
			case "create_service":
				confirm_create_service("service_created", (str_request.split("|"))[1]);
				break;
			case "remove_service":
				index = program_names.indexOf((str_request.split("|"))[1]);
				program_names.splice(index, 1); // remove device from device_names
				confirm_remove_program("service_removed");
				break;
			case "edit_service":
				confirm_edit_service("service_edited", (str_request.split("|"))[1]);
				break;
			case "wait_for_signal":
				half_time_ms = add_device_countdown * 1000 / 2;
				setTimeout(function(){confirm_wait_for_signal("signal_detected|001|42")}, half_time_ms); // makes use of dummy values
				break;
			case "add_device":
				device_names.push((str_request.split("|"))[3]);
				confirm_add_device("device_added");
				break;
			case "remove_device":
				index = device_names.indexOf((str_request.split("|"))[1]);
				device_names.splice(index, 1); // remove device from device_names
				confirm_remove_device("device_removed");
				break;
			case "do_action":
				confirm_do_action("action_complete");
				break;
			case "get_status":
				load_status((str_request.split("|"))[2], "Lookin' good!");
				break;
			case "edit_user_name":
				confirm_edit_username("name_edited");
				break;
			case "edit_password":
				confirm_edit_password("password_edited");
				break;
			default: break;
		}
		return;
	}
	
	// show loading indicator when request is made
	document.getElementById("loading_indicator").style.display = "block";
	
	xhr.onreadystatechange = function()
	{
		if (xhr.readyState==4 && xhr.status==200) // when response is received and everything is "OK"...
		{
			// hide loading indicator when response is received
			document.getElementById("loading_indicator").style.display = "none";
		
			str_response = xhr.responseText;
			if (str_response == "not_logged_in")
			{
				window.location.href = base_url + "login.html";
				return;
			}
			switch (op)
			{
				case "message_board":                                 // retrieve message board notifications (*)
					update_message_board(str_response); break;
					
				case "device_list":                                   // retrieve list of devices (*)
					if (misc == "addEditProgram")
						display_devices(str_response);
					else
						update_list(true, str_response, misc);
					break;
					
				case "device_info":                                   // retrieve device information (*)
					if (misc == "addEditProgram")
						update_dev(str_response, (str_request.split("|"))[1]);
					else
						load_device_info(str_response);
					break;
					
				case "service_list":                                  // retrieve list of services (*)
					update_list(false, str_response, misc); break;
					
				case "service_info":                                  // retrieve condensed service information (*)
					popup_view_open(str_response); break;
					
				case "service_representation":                        // retrieve full service information (*)
					update_prog_lists(str_response, misc); break;
					
				case "create_service":                                // create service (*)
					confirm_create_service(str_response, (str_request.split("|"))[1]); break;
					
				case "remove_service":                                // remove service (*)
					confirm_remove_program(str_response); break;
					
				case "edit_service":                                  // edit service (*)
					confirm_edit_service(str_response, (str_request.split("|"))[1]); break;
					
				case "edit_service_name":                             // edit service name
					confirm_edit_service_name(str_response); break;
					
				case "wait_for_signal":                               // wait for new device signal (*)
					confirm_wait_for_signal(str_response); break;
					
				case "add_device":                                    // add device (*)
					confirm_add_device(str_response); break;
					
				case "remove_device":                                 // remove device (*)
					confirm_remove_device(str_response); break;
					
				case "do_action":                                     // do action (*)
					confirm_do_action(str_response);
					
				case "configure_device":                              // configure device
					confirm_configure_device(str_response); break;
					
				case "get_status":                                    // get status variable from device (*)
					load_status((str_request.split("|"))[2], str_response); break;
					
				case "edit_device_name":                              // edit device name
					confirm_edit_device_name(str_response); break;
				
				case "edit_user_name":                                // edit username (*)
					confirm_edit_username(str_response); break;
					
				case "edit_password":                                 // edit password (*)
					confirm_edit_password(str_response); break;
				
				default: break;
			}
		}
	}
	
	xhr.open("POST", "program_data", async); // request file using POST
	xhr.send(str_request);
}

function get_page(page, query)
{
	// show loading indicator when page is requested
	document.getElementById("loading_indicator").style.display = "block";

	switch (page)
	{
		case "index":              window.location.href = base_url + "index.html"; break;
		case "devices":            window.location.href = base_url + "devices.html"; break;
		case "deviceControlPanel": window.location.href = base_url + "deviceControlPanel.html?device=" + query; break;
		case "programs":           window.location.href = base_url + "programs.html"; break;
		case "addEditProgram":     window.location.href = base_url + "addEditProgram.html?program=" + query; break;
		case "upload":             window.location.href = base_url + "uploadDescriptor.html"; break;
		case "account":            window.location.href = base_url + "account.html"; break;
		case "logout":             window.location.href = base_url + "logout.html"; break;
		default:                   window.location.href = base_url + "index.html"; break;
	}
}

// The following function is invoked for devices and programs

function update_list(dev_progn, string_in, page) // dev_progn: true = devices page, false = programs page
{
	var temp = string_in.split("|");
	var string = "";
	
	if (dev_progn) // devices page
		device_names = temp.slice(0);  // copy to global variable
	else // programs page
		program_names = temp.slice(0); // copy to global variable
	
	if (page) // should web page be updated?
	{
		if (dev_progn) update_page(true);  // devices page
		else           update_page(false); // programs page
	}
}

// The following function is invoked for devices and programs

function update_page(dev_progn) // dev_progn: true = devices page, false = programs page
{
	var html = ""; // main body of HTML code to be inserted into page
	var name;
	var temp;
	if (dev_progn) temp = device_names;
	else           temp = program_names;
	
	if (temp[0] == "no_devices" || temp[0] == "no_services") // empty list
	{
		if (dev_progn) html = "-- No Devices --";
		else           html = "-- No Programs --";
	}
	else // list has items
	{
		html = "<table border='1' style='width:100%;border:3px solid black;border-collapse:collapse;'>"; // establish table
		for (i = 0; i < temp.length; i++) // cycles through all devices or programs
		{
			name = temp[i];
			html = html + "<tr><td><div class='col_1'>" + name + "</div><div class='col_2'>";
			if (dev_progn) // device
			{
				html = html + "<button type='button' class='button_list' onclick='get_page(\"deviceControlPanel\", \"" + name + "\")'>Configure</button>" +
				                      "<button type='button' class='button_list' onclick='popup_remove_device_open(\"" + name + "\")'>Remove</button>";
			}
			else // program
			{
				html = html + "<button type='button' class='button_list' onclick='get_program_info(\"" + name + "\")'>View</button>" +
				                      "<button type='button' class='button_list' onclick='get_page(\"addEditProgram\", \"" + name + "\")'>Edit</button>" +
				                      "<button type='button' class='button_list' onclick='popup_remove_program_open(\"" + name + "\")'>Remove</button>";
			}
			html = html + "</div></td></tr>";
		}
		html = html + "</table>";
	}
	
	// insert HTML code
	if (dev_progn) document.getElementById("device_list").innerHTML = html;
	else           document.getElementById("program_list").innerHTML = html;
}

/*
function clean_whitespace(node)
{
	for (i = 0; i < node.childNodes.length; i++)
	{
		curr = node.childNodes[i];
		if (curr.nodeType == 3 && !/\S/.test(curr.nodeValue))
		{
			node.removeChild(node);
			i--;
		}
		if(curr.nodeType == 1)
		{
			clean_whitespace(curr);
		}
	}
	return node;
}
*/

function indeces_of(str, sym)
{
	var indeces = new Array();
	var temp = str.indexOf(sym);
	var offset = 0;
	var remaining;
	while (temp != -1)
	{
		temp = temp + offset;
		indeces.push(temp);
		remaining = str.substring(temp + 1);
		offset = offset + temp + 1;
		temp = remaining.indexOf(sym);
	}
	return indeces;
}

/**************************************************************************************************
 *                                                                                                *
 *                    INDEX PAGE                                                                  *
 *                                                                                                *
 **************************************************************************************************/

function update_message_board(notifications)
{
	var not_array = notifications.split("|");
	var string = "";
	if (not_array[0] == "no_notifications")
		string = "-- No Notifications --";
	else
	{
		for (i = 0; i < not_array.length; i++)
		{
			string = string + not_array[i] + "<br><br>";
		}
	}
	document.getElementById("messages").innerHTML = string;
}

/**************************************************************************************************
 *                                                                                                *
 *                    DEVICES PAGE                                                                *
 *                                                                                                *
 **************************************************************************************************/

var device_names = ["no_devices"]; // default
var countdown_off = true;          // used by the add device process
var new_device_type_ID = -1;       // used by the add device process
var new_device_ID = -1;            // used by the add device process

function popup_add_device_open()
{
	// show popup and darken background
	document.getElementById("popup_add_device").style.display = "block";
	document.getElementById("dark_bkgnd").style.display = "block";
	
	popup_add_stage(1);
}

function popup_add_device_close()
{
	// hide popup and brighten background
	document.getElementById("popup_add_device").style.display = "none";
	document.getElementById("dark_bkgnd").style.display = "none";
}

function popup_add_stage(stage) // stages: 1 = start, 2 = wait, 3 = timeout, 4 = signal
{
	// activate or deactivate popup back button
	if (stage == 1) // stage: start
		document.getElementById("button_popup_add_back").style.display = "block";
	else // stages: wait, timeout, signal
		document.getElementById("button_popup_add_back").style.display = "none";
	
	// deactivate all stages
	document.getElementById("popup_add_1_start").style.display = "none";
	document.getElementById("popup_add_2_wait").style.display = "none";
	document.getElementById("popup_add_3_timeout").style.display = "none";
	document.getElementById("popup_add_4_signal").style.display = "none";
	
	// activate appropriate stage
	switch (stage)
	{
		case 1:  document.getElementById("popup_add_1_start").style.display = "block"; break;
		case 2:  document.getElementById("popup_add_2_wait").style.display = "block"; break;
		case 3:  document.getElementById("popup_add_3_timeout").style.display = "block"; break;
		case 4:  document.getElementById("popup_add_4_signal").style.display = "block"; break;
		default: document.getElementById("popup_add_1_start").style.display = "block"; break;
	}
	
	// special functions
	if (stage != 2) // all stages except wait
		countdown_off = true; // stop timer
	if (stage == 2) // wait
		timer_init(); // start timer
	if (stage == 4)
		document.getElementById("add_device_error").innerHTML == ""; // clear error message
}

function timer_init()
{
	var template = "%%S%% seconds";
	var request;
	
	request = "wait_for_signal|" + add_device_countdown;
	http_post_request(request, true, "");
	
	countdown_off = false;
	timer(add_device_countdown, template);
}

function timer(remaining, template)
{
	var n, string;

	if (countdown_off) return;
	
	remaining = remaining - 1;
	if (remaining > 0)
	{
		n = remaining.toString();
		string = template.replace("%%S%%", n);
		document.getElementById("countdown").innerHTML = string;
		setTimeout(function(){timer(remaining, template)}, 1000);
	}
	else
	{
		popup_add_stage(3);
	}
}

function confirm_wait_for_signal(response)
{
	var html;
	
	if (countdown_off) return;

	response = response.split("|");
	new_device_type_ID = response[1];
	new_device_ID = response[2];
	
	switch (response[0])
	{
		case "signal_detected":
			html = "Device Type: " + new_device_type_ID + "<br>Device ID: " + new_device_ID;
			document.getElementById("device_data").innerHTML = html;
			popup_add_stage(4);
			break;
		default: // no_signal_detected
			alert(response);
			break;
	}
}

function add_device_attempt()
{
	var desired_name = document.getElementById("desired_name").value;
	var i, j;
	
	// check for nothing
	if (desired_name == "")
	{
		add_device_error(0); // 0 = empty field
		return;
	}
	
	// check for delimiters
	i = desired_name.indexOf("|");
	j = desired_name.indexOf("`");
	if (i > -1 || j > -1)
	{
		add_device_error(1); // 1 = invalid characters
		return;
	}
	
	// check for invalid characters
	if (invalid_characters(desired_name))
	{
		add_device_error(1);
		return;
	}
	
	// check for duplicates
	for (var index = 0; index < device_names.length; index++)
	{
		if (device_names[index] == desired_name)
		{
			add_device_error(2); // 2 = desired name is already in use
			return;
		}
	}
	
	// add device (all tests passed)
	add_device(desired_name);
}

function add_device_error(error_code) // 0 = empty field, 1 = invalid characters, 2 = name already in use
{
	switch (error_code)
	{
		case 0:
			document.getElementById("add_device_error").innerHTML = "<b>Please type characters into the field above.</b>"
			break;
		case 1:
			document.getElementById("add_device_error").innerHTML = "<b>Please use only letters, numbers, spaces, and following characters:<br>. - ~ _</b>";
			break;
		case 2:
			document.getElementById("add_device_error").innerHTML = "<b>That name is already in use. Please enter a unique name.</b>"
			break;
		default:
			document.getElementById("add_device_error").innerHTML = "<b>Internal error. Please contact a developer.</b>"
			break;
	}
}

function invalid_characters(dev_name)
{
	var c;
	var html_message;

	for (var i = 0; i < dev_name.length; i++)
	{
		c = dev_name[i];
		if ( (c < "A" || c > "Z") &&                         // "not an uppercase letter"
		     (c < "a" || c > "z") &&                         // "not a lowercase letter"
			 (c < "0" || c > "9") &&                         // "not a number"
			 c != "." && c != "-" && c != "~" && c != "_" && // "not an allowed URL character"
			 c != " " && c != "'" )                          // "not an allowed application character"
		{
			return true;
		}
	}
	
	// if execution reaches this point, the user's desired program name is a-okay
	
	return false;
}

function add_device(name)
{
	var request = "add_device|" + new_device_type_ID + "|" + new_device_ID + "|" + name;
	http_post_request(request, true, "");
}

function confirm_add_device(response)
{
	switch (response)
	{
		case "device_added":
			if (!server_mode)
				update_page(true);
			else
				http_post_request("device_list", true, true); // update list and update web page
			popup_add_device_close();
			break;
		default: // invalid_name, add_failed, invalid_num_inputs
			alert(response);
			break;
	}
}

function popup_remove_device_open(device_name)
{
	var html = "Are you sure you want to remove<br>device " + device_name + "?<br><br>";
	document.getElementById("message_remove").innerHTML = html;
	document.getElementById("button_remove_device").onclick = function() {remove_device(device_name)};

	// show popup and darken background
	document.getElementById("popup_remove_device").style.display = "block";
	document.getElementById("dark_bkgnd").style.display = "block";
}

function popup_remove_device_close()
{
	// hide popup and brighten background
	document.getElementById("popup_remove_device").style.display = "none";
	document.getElementById("dark_bkgnd").style.display = "none";
}

function remove_device(device_name)
{
	request = "remove_device|" + device_name;
	http_post_request(request, true, "");
}

function confirm_remove_device(response)
{
	switch (response)
	{
		case "device_removed":
			if (!server_mode)
				update_page(true);
			else
				http_post_request("device_list", true, true); // update list and update web page
			popup_remove_device_close();
			break;
		default: // removal_failed, invalid_name, invalid_num_inputs
			alert(response);
			break;
	}
}

/**************************************************************************************************
 *                                                                                                *
 *                    DEVICE CONTROL PANEL PAGE                                                   *
 *                                                                                                *
 **************************************************************************************************/

function load_control_panel()
{
	var query = window.location.search;
	device_name = query.substring(8); // get rid of leading "?device="
	device_name = device_name.replace(/%20/g, " "); // replace all instances of %20 with a space
	device_name = device_name.replace(/%27/g, "'"); // replace all instances of %27 with an apostrophe
	
	document.title = device_name;
	document.getElementById("device_name_heading").innerHTML = device_name;
	
	var request = "device_info|" + device_name;
	http_post_request(request, true, "");
}

function load_device_info(xml_string)
{
	var device_name, parser, doc;
	var type, num_actions, num_statuses;
	var actions, status_names;
	var name, func, arg;
	var arg_name, arg_type, arg_id;
	var html;
	var f_name, f_id;
	var request;
	
	device_name = document.title;
	parser = new DOMParser();
	doc = parser.parseFromString(xml_string, "text/xml");
	// clean_whitespace(doc.getElementsByTagName("peripheral")[0]);
	
	num_actions = 0;
	num_statuses = 0;
	
	type = doc.getElementsByTagName("peripheral-type")[0].childNodes[0].nodeValue;
	if (doc.getElementsByTagName("actions")[0] != undefined)
		num_actions = doc.getElementsByTagName("actions")[0].childNodes.length;
	if (doc.getElementsByTagName("statuses")[0] != undefined)
		num_statuses = doc.getElementsByTagName("statuses")[0].childNodes.length;
	
	// EXTRACT ACTIONS
	// Each element of actions is an action represented by an array of the following form:
	// [name, function, function_argument] where function_argument is either undefined of an array of the following form:
	// [argument_name, argument_type, argument_id]
	actions = new Array();
	for (var i = 0; i < num_actions; i++)
	{
		name = doc.getElementsByTagName("actions")[0].childNodes[i].childNodes[0].childNodes[0].nodeValue;
		func = doc.getElementsByTagName("actions")[0].childNodes[i].childNodes[1].getAttribute("id");
		arg =  doc.getElementsByTagName("actions")[0].childNodes[i].childNodes[1].childNodes[0];
		if (arg != undefined)
		{
			arg_name = arg.childNodes[0].nodeValue;
			arg_type = arg.getAttribute("type");
			arg_id =   arg.getAttribute("id");
			arg = [arg_name, arg_type, arg_id];
		}
		actions[i] = [name, func, arg];
	}
	
	// EXTRACT STATUS NAMES
	// Note: only status names are extracted here, actual statuses are requested later
	status_names = new Array();
	for (i = 0; i < num_statuses; i++)
	{
		status_names[i] = doc.getElementsByTagName("statuses")[0].childNodes[i].childNodes[0].childNodes[0].nodeValue;
	}
	
	// DISPLAY INFORMATION
	html = "<b>Device Type ID:</b> " + type + "<br><br>";
	
	// display statuses (function load_status replaces the text in the spans)
	if (status_names.length > 0)
	{
		html = html + "<b>Statuses:</b><br>"
		for (i = 0; i < status_names.length; i++)
		{
			html = html + status_names[i] + ": <span id='" + status_names[i] + "'>-- Cannot reach server --</span><br>";
		}
		html = html + "<br>";
	}
	
	// display actions
	if (actions.length > 0)
	{
		html = html + "<b>Actions:</b><br><br>"
		for (i = 0; i < actions.length; i++)
		{
			html = html + actions[i][0] + "<br>"; // necessary?
			
			f_name = actions[i][0];
			
			if (actions[i][2] == undefined) // function_argument is undefined, therefore control type is a push button
			{
				html = html +
				"<button type='button' onclick='do_action(\"" + device_name + "\", \"" + actions[i][0] + "\", \"\")'>" + f_name + "</button><br>";
			}
			
			else // function_argument is not undefined, therefore control type depends on argument_type
			{
				f_id = actions[i][2][2];
			
				switch (actions[i][2][1]) // argument_type
				{
					case "Integer":
						html = html +
						"<input type='text' id='input_action_" + i + "'> (" + f_id + ")<br>" +
						"<button type='button' onclick='do_action_attempt(\"input_action_" + i + "\", \"Integer\", \"" + device_name + "\", \"" + f_name + "\")'>" + f_name + "</button><br>";
						break;
					case "Floating-Point":
						html = html +
						"<input type='text' id='input_action_" + i + "'> (" + f_id + ")<br>" +
						"<button type='button' onclick='do_action_attempt(\"input_action_" + i + "\", \"Floating-Point\", \"" + device_name + "\", \"" + f_name + "\")'>" + f_name + "</button><br>";
						break;
					case "Boolean":
						html = html +
						"<button type='button' onclick='do_action_attempt(1, \"Boolean\", \"" + device_name + "\", \"" + f_name + "\")'>Turn On</button>" +
						"<button type='button' onclick='do_action_attempt(0, \"Boolean\", \"" + device_name + "\", \"" + f_name + "\")'>Turn Off</button><br>";
						break;
					case "Date":
					
						break;
					case "Enumerator":
					
						break;
					default: // "String"
						html = html +
						"<input type='text' id='input_action_" + i + "'> (" + f_id + ")<br>" +
						"<button type='button' onclick='do_action_attempt(\"input_action_" + i + "\", \"String\", \"" + device_name + "\", \"" + f_name + "\")'>" + f_name + "</button><br>";
						break;
				}
			}
			html = html + "<br>"
		}
	}
	
	document.getElementById("content").innerHTML = html;
	
	// REQUEST & LOAD STATUSES
	for (i = 0; i < num_statuses; i++)
	{
		request = "get_status|" + device_name + "|" + status_names[i];
		http_post_request(request, false, ""); // synchronously request statuses
	}
}

function load_status(status_name, status)
{
	if (status == "invalid_name" || status == "invalid_status" || status == "invalid_num_inputs")
	{
		alert("Status: " + status);
		return;
	}
	document.getElementById(status_name).innerHTML = status + "<br>";
}

function do_action_attempt(input, type, device_name, func) // purpose: grab and check user input
{
	var no_error = true;
	var value;
	
	switch (type)
	{
		case "Integer":
			value = document.getElementById(input).value; // grab
			for (i = 0; i < value.length; i++) if (value[i] < '0' || value[i] > '9') no_error = false; // check
			break;
		case "Floating-Point":
			value = document.getElementById(input).value; // grab
			if (value.isNaN()) no_error = false; // check
			break;
		case "Boolean":
			if (input == 1) value = "true";
			else            value = "false";
			// no check
			break;
		default: // "String"
			value = document.getElementById(input).value; // grab
			// no check
			break;
	}
	
	if (no_error)
		do_action(device_name, func, value);
	else // error
		alert("Input error.");
}

function do_action(device_name, func, args)
{
	var extra;
	var request;

	extra = "";
	if (args != "") extra = "|" + args;

	request = "do_action|" + device_name + "|" + func + extra;
	http_post_request(request, true, "");
}

function confirm_do_action(response)
{
	var device_name;
	var request;

	switch (response)
	{
		case "action_complete":
			device_name = document.title;
			request = "device_info|" + device_name;
			http_post_request(request, true, "");
			break;
		default: // invalid_peripheral_name, invalid_action, invalid_num_inputs
			alert(response);
			break;
	}
}
 
/**************************************************************************************************
 *                                                                                                *
 *                    PROGRAMS PAGE                                                               *
 *                                                                                                *
 **************************************************************************************************/

var program_names = ["no_services"]; // default

function get_program_info(program_name)
{
	var request = "service_info|" + program_name;
	http_post_request(request, true, "");
}

function popup_view_open(program_info)
{
	var program_info, program_name, program_trigs, program_acts;
	var curr;
	var html;
	var device, name, arg;

	// check
	if (program_info == "invalid_name" || program_info == "invalid_num_inputs")
	{
		alert(program_info);
		return;
	}
	
	// [service name]|
	//                t`[device name]`[trigger name]`[arg1]`[arg2]`...|...
	//                a`[device name]`[action name]`[arg1]`[arg2]`...|...
	// Note: Triggers and actions could appear in any order
	
	program_info = program_info.split("|");
	program_name = program_info[0];
	program_trigs = new Array();
	program_acts = new Array();
	
	// extract triggers and actions
	for (i = 1; i < program_info.length; i++)
	{
		curr = program_info[i].split("`");
		if (curr[0] == "t") // trigger
		{
			program_trigs.push(curr);
		}
		else // action
		{
			program_acts.push(curr);
		}
	}
	
	// DISPLAY PROGRAM INFORMATION
	
	// program name
	html = "<b>Program: </b>" + program_name + "<br><br>";
	
	// program triggers
	html = html + "<b>Triggers:</b><ol>";
	for (var i = 0; i < program_trigs.length; i++)
	{
		device = program_trigs[i][1];
		name =   program_trigs[i][2];
		arg =    program_trigs[i][3];
		html = html + "<li>" + device + ": " + name;
		if (arg != undefined) html = html + " " + arg;
		html = html + "</li>";
	}
	html = html + "</ol>";
	
	// program actions
	html = html + "<b>Actions:</b><ol>";
	for (i = 0; i < program_trigs.length; i++)
	{
		device = program_acts[i][1];
		name =   program_acts[i][2];
		arg =    program_acts[i][3];
		html = html + "<li>" + device + ": " + name;
		if (arg != undefined) html = html + " " + arg;
		html = html + "</li>";
	}
	html = html + "</ol>";
	
	// replace html
	document.getElementById("popup_view_content").innerHTML = html;
	
	// update button onclick function
	document.getElementById("button_popup_view_edit").onclick = function() {get_page("addEditProgram", program_name)}; 
	
	// show popup and darken background
	document.getElementById("popup_view_program").style.display = "block";
	document.getElementById("dark_bkgnd").style.display = "block";
}

function popup_view_close()
{
	// hide popup and brighten background
	document.getElementById("popup_view_program").style.display = "none";
	document.getElementById("dark_bkgnd").style.display = "none";
}

function popup_remove_program_open(program_name)
{
	var html;

	html = "Are you sure you want to remove<br>program " + program_name + "?<br><br>";
	document.getElementById("message_remove").innerHTML = html;
	document.getElementById("button_remove_program").onclick = function() {remove_program(program_name)};

	// show popup and darken background
	document.getElementById("popup_remove_program").style.display = "block";
	document.getElementById("dark_bkgnd").style.display = "block";
}

function popup_remove_program_close()
{
	// hide popup and brighten background
	document.getElementById("popup_remove_program").style.display = "none";
	document.getElementById("dark_bkgnd").style.display = "none";
}

function remove_program(program_name)
{
	var request = "remove_service|" + program_name;
	http_post_request(request, true, "");
}

function confirm_remove_program(response)
{
	switch (response)
	{
		case "service_removed":
			if (!server_mode)
				update_page(false);
			else
				http_post_request("service_list", true, true); // update list and update web page
			popup_remove_program_close();
			break;
		default: // removal_failed, invalid_name, invalid_num_inputs
			alert(response);
			break;
	}
}

/**************************************************************************************************
 *                                                                                                *
 *                    ADD/EDIT PROGRAM PAGE                                                       *
 *                                                                                                *
 **************************************************************************************************/

var triggers = new Array(); // list of currently active triggers
var actions =  new Array(); // list of currently active actions
var dev = new Object(); // contains information regarding a device the user wishes to use for a trigger or an action

function load_edit_panel()
{
	var query;
	var program_name;
	var request;
	var html_trigs, html_acts;

	query = window.location.search;
	program_name = query.substring(9); // get rid of leading "?program="
	program_name = program_name.replace(/%20/g, " "); // replace all instances of %20 with a space
	program_name = program_name.replace(/%27/g, "'"); // replace all instances of %27 with an apostrophe
	
	if (program_name == "") program_name = "New Program";
	
	// set title
	document.title = program_name;
	document.getElementById("program_name_heading").innerHTML = program_name;
	
	// request list of devices
	request = "device_list"
	http_post_request(request, false, "addEditProgram"); // synchronously request list of devices
	
	if (program_name == "New Program") // user is creating a new program
	{
		html_trigs = "-- No Triggers --";
		html_acts = "-- No Actions --";
		document.getElementById("trigger_list").innerHTML = html_trigs;
		document.getElementById("action_list").innerHTML = html_acts;
	}
	else // user is editing an existing program
	{
		request = "service_representation|" + program_name;
		http_post_request(request, true, 1);
	}
}

function update_prog_lists(response, page)
{
	// Note: The current version of this function allows for multiples of the same action to exist

	var prog_rep;
	var temp_prog, temp_act, temp_trigs;
	var action;

	// check for errors
	if (response == "invalid_name" || response == "invalid_num_inputs")
	{
		alert(response);
		return;
	}
	else prog_rep = response;
	
	// remove all instances of "::"
	prog_rep = prog_rep.replace(/::/g, "");
	
	// split representation into individual T->A components
	prog_rep = prog_rep.split(";");
	
	// here's where the magic needs to happen
	for (i = 0; i < prog_rep.length; i++) // cycles through all T->A components
	{
		temp_prog = prog_rep[i]; // temporary variable
		temp_prog = temp_prog.split("->"); // separate trigger(s) from action
		
		// let's look at this from the perspective of the action
		
		temp_act = temp_prog[1]; // temporary variable
		temp_act = temp_act.split(":"); // separate elements
		
		// create new action
		action =           new Object();      // boom
		action.type =      "a";               // (aids searches)
		
		// assign properties to new action
		action.device =    temp_act[0];       // first element
		action.operation = temp_act[1];       // second element
		if (temp_act.length > 2)
		action.args =      temp_act.slice(2); // remaining elements
		
		// ... now for the trigger(s)
		
		temp_trigs = temp_prog[0]; // temporary variable
		temp_trigs = temp_trigs.substring(1, temp_trigs.length-1); // remove outermost brackets
		action.fanin = trigger_handler(temp_trigs, action); // aquire children
		
		actions.push(action); // add action to list
	}
	
	if (page) update_prog_page();
}

function trigger_handler(trigs, fanout) // recursive function
{
	var trigs_new;
	var trigger, check, node;
	var pos_ands, pos_ors;
	var pos_ands_ext, pos_ors_ext;
	var temp_str, num_op_bracks, num_cl_bracks;
	var start, end;

	// END CASE: trigs is in fact a single trigger
	
	if (trigs.indexOf("&") == -1 && trigs.indexOf("+") == -1)
	{
		trigs = trigs.split(":"); // separate elements
		
		// create new trigger
		trigger =           new Object();   // boom
		trigger.type =      "t";            // (aids searches)
		
		// assign properties to new trigger
		trigger.device =    trigs[0];       // first element
		trigger.condition = trigs[1];       // second element
		if (trigs.length > 2)
		trigger.args =      trigs.slice(2); // remaining elements
		
		// check to see if trigger already exists
		check = get_trigger(trigger); // if found, returns trigger; if not found, returns null
		if (check != null) // trigger was found
		{
			trigger = check; // use already existing trigger
			trigger.fanout.push(fanout); // add new pathway to already existing array of pathways
		}
		else // trigger was not found
		{
			trigger.fanout = new Array(); // create new array of pathways
			trigger.fanout.push(fanout); // add new pathway to new array of pathways
		}
		
		triggers.push(trigger); // add trigger to list
		
		return trigger; // return trigger to parent
	}
	
	// RECURSIVE CASE: trigs contains multiple triggers
	
	// find positions of all & and + symbols
	pos_ands = indeces_of(trigs, "&");
	pos_ors = indeces_of(trigs, "+");
	
	trigs_new = new Array();
	
	if (pos_ands.length > 0) // & symbols were found (NOTE: It is still possible that no EXTERNAL & symbols are present)
	{
		pos_ands_ext = new Array(); // positions of all & symbols that are NOT inside any brackets
	
		for (var i = 0; i < pos_ands.length; i++) // cycle through all & symbols
		{
			// grab everything in front of the current & symbol
			temp_str = trigs.substring(0, pos_ands[i]);
			
			// determine the number of opening and closing brackets in temp_str
			num_op_bracks = indeces_of(temp_str, "(").length;
			num_cl_bracks = indeces_of(temp_str, ")").length;
			
			// if the number of opening brackets matches the number of closing brackets,
			// the current & symbol is NOT inside any brackets and can therefore be used
			// to split trigs
			if (num_op_bracks == num_cl_bracks) pos_ands_ext.push(pos_ands[i]);
		}
		
		if (pos_ands_ext.length > 0) // external & symbols are indeed present
		{
			// first piece
			start = 0;               // included
			end =   pos_ands_ext[0]; // up to but not included
			trigs_new.push(trigs.substring(start, end));
			
			for (var j = 1; j < pos_ands_ext.length-1; j++)
			{
					// middle piece(s)
					start = pos_ands_ext[j-1] + 1; // included
					end =   pos_ands_ext[j]        // up to but not included
					trigs_new.push(trigs.substring(start, end));
			}
				
			// last piece
			start = pos_ands_ext[pos_ands_ext.length-1] + 1; // included
			end =   trigs.length;                            // up to but not included
			trigs_new.push(trigs.substring(start, end));
			
			node = new Object();
			node.type = "&";
			node.fanin = new Array();
			node.fanout = fanout;
			
			for (var k = 0; k < trigs_new.length; k++)
			{
				temp_str = trigs_new[k];
				temp_str = temp_str.substring(1, temp_str.length-1); // remove outermost brackets
				node.fanin.push(trigger_handler(temp_str, node)); // recursive call; acquire children
			}
			
			return node;
		}
	}
	else // no & symbols were found (NOTE: It is still possible that some + symbols are NOT external)
	{
		pos_ors_ext = new Array(); // positions of all + symbols that are NOT inside any brackets
		
		for (i = 0; i < pos_ors.length; i++) // cycle through all + symbols
		{
			// grab everything in front of the current + symbol
			temp_str = trigs.substring(0, pos_ors[i]);
			
			// determine the number of opening and closing brackets in temp_str
			num_op_bracks = indeces_of(temp_str, "(").length;
			num_cl_bracks = indeces_of(temp_str, ")").length;
			
			// if the number of opening brackets matches the number of closing brackets,
			// the current & symbol is NOT inside any brackets and can therefore be used
			// to split trigs
			if (num_op_bracks == num_cl_bracks) pos_ors_ext.push(pos_ors[i]);
		}
		
		// since no & symbols were found, therefore external + symbols must be present
		
		// first piece
		start = 0;              // included
		end =   pos_ors_ext[0]; // up to but not included
		trigs_new.push(trigs.substring(start, end));
		
		for (j = 1; j < pos_ors_ext.length-1; j++)
		{
				// middle piece(s)
				start = pos_ors_ext[j-1] + 1; // included
				end =   pos_ors_ext[j]        // up to but not included
				trigs_new.push(trigs.substring(start, end));
		}
			
		// last piece
		start = pos_ors_ext[pos_ors_ext.length-1] + 1; // included
		end =   trigs.length;                          // up to but not included
		trigs_new.push(trigs.substring(start, end));
		
		node = new Object();
		node.type = "+";
		node.fanin = new Array();
		node.fanout = fanout;
		
		for (k = 0; k < trigs_new.length; k++)
		{
			temp_str = trigs_new[k];
			temp_str = temp_str.substring(1, temp_str.length-1); // remove outermost brackets
			node.fanin.push(trigger_handler(temp_str, node)); // recursive call; acquire children
		}
		
		return node;
	}
}

function update_prog_page()
{
	var html_trigs, html_acts;
	var d, c, o, a;

	html_trigs = "<table border='1' style='width:100%;border:3px solid black;border-collapse:collapse;'>"; // establish table
	for (var i = 0; i < triggers.length; i++)
	{
		d = triggers[i].device;
		c = triggers[i].condition;
		a = triggers[i].args;
		
		html_trigs = html_trigs + "<tr><td><div class='col_1'><b>" + (i+1) + ":</b> " + d + ": " + c;
		if (a != undefined)
		{
			for (var j = 0; j < a.length; j++)
			{
				html_trigs = html_trigs + " - " + a[j];
			}
		}
		html_trigs = html_trigs + "</div><div class='col_2'>" +
		                          "<button type='button' class='button_list' onclick='view_trigger(" + i + ")'>View</button>" +
								  "<button type='button' class='button_list' onclick='remove_trigger_attempt(" + i + ")'>Remove</button>" +
								  "</div></td></tr>";
	}
	html_trigs = html_trigs + "</table>";
	
	html_acts = "<table border='1' style='width:100%;border:3px solid black;border-collapse:collapse;'>"; // establish table
	for (var i = 0; i < actions.length; i++)
	{
		d = actions[i].device;
		o = actions[i].operation;
		a = actions[i].args;
		
		html_acts = html_acts + "<tr><td><div class='col_1'><b>" + (i+1) + ":</b> " + d + ": " + o;
		if (a != undefined)
		{
			for (var j = 0; j < a.length; j++)
			{
				html_acts = html_acts + " - " + a[j];
			}
		}
		html_acts = html_acts + "</div><div class='col_2'>" +
		                          "<button type='button' class='button_list' onclick='view_action(" + i + ")'>View</button>" +
								  "<button type='button' class='button_list' onclick='popup_remove_action_open(" + i + ")'>Remove</button>" +
								  "</div></td></tr>";
	}
	html_acts = html_acts + "</table>";
	
	document.getElementById("trigger_list").innerHTML = html_trigs;
	document.getElementById("action_list").innerHTML = html_acts;
}

/*** PROCESS: VIEW / REMOVE TRIGGER OR ACTION ***/

function view_trigger(trig_num)
{
	var trig;
	var num_paths;
	var acts;
	var start_node;
	var html_title, html_message, html_buttons;
	var d, o, a;
	
	trig = triggers[trig_num];
	num_paths = trig.fanout.length;
	
	acts = new Array();
	if (num_paths > 0) // trigger might be attached to at least one action
	{
		for (var i = 0; i < num_paths; i++)
		{
			start_node = trig.fanout[i];
			temp = get_action(start_node);
			if (temp != null) acts.push(temp);
		}
	}
	
	number_actions();
	
	html_title = "View Trigger";
	if (acts.length > 0)
	{
		html_message = "Trigger " + (trig_num+1) + " is attached to the following actions:<br>";
		for (var i = 0; i < acts.length; i++)
		{
			html_message = html_message + "<b>" + (acts[i].number+1) + "</b><br>";
		}
	}
	else
	{
		html_message = "Trigger " + (trig_num+1) + " isn't attached to any actions yet.";
	}
	html_message = html_message + "<br>";
	html_buttons = "<button type='button' onclick='popup_all_purpose_close()'>Close</button>";
	
	popup_all_purpose_open([html_title, html_message, html_buttons], false);
}

function view_action(act_num)
{
	var action;
	var rep;
	var html_title, html_message, html_buttons;
	
	action = actions[act_num];
	
	number_triggers();
	
	html_title = "View Action";
	html_message = "Action <b>" + (act_num+1) + "</b> will occur when the logical relationship involving the following triggers is satisfied:<br><br>" +
	               string_generator(action.fanin) + "<br><br>";
	html_buttons = "<button type='button' onclick='popup_all_purpose_close()'>Close</button>";
	
	
	popup_all_purpose_open([html_title, html_message, html_buttons], false);
}

function string_generator(node)
{
	var string;
	
	// END CASE: node is a single trigger
	
	if (node.type == "t")
	{
		string = "<b>" + (node.number+1) + "</b>";
		return string;
	}
	
	// RECURSIVE CASE 1: node is a "&" node
	
	if (node.type == "&")
	{
		string = "";
	
		num_children = node.fanin.length;
		for (var i = 0; i < num_children; i++)
		{
			if (i < num_children-1) // child is not last child
				string = string + "(" + string_generator(node.fanin[i]) + ") <b>AND</b> ";
			else // child is last child
				string = string + "(" + string_generator(node.fanin[i]) + ")";
		}
		
		return string;
	}
	
	// RECURSIVE CASE 2: node is a "+" node
	
	if (node.type == "+")
	{
		string = "";
	
		num_children = node.fanin.length;
		for (var i = 0; i < num_children; i++)
		{
			if (i < num_children-1) // child is not last child
				string = string + "(" + string_generator(node.fanin[i]) + ") <b>OR</b> ";
			else // child is last child
				string = string + "(" + string_generator(node.fanin[i]) + ")";
		}
		
		return string;
	}
}

function remove_trigger_attempt(trig_num)
{
	var trig;
	var num_paths;
	var acts;
	var start_node;
	
	trig = triggers[trig_num];
	num_paths = trig.fanout.length;
	
	acts = new Array();
	if (num_paths > 0) // trigger might be attached to at least one action
	{
		for (var i = 0; i < num_paths; i++)
		{
			start_node = trig.fanout[i];
			temp = get_action(start_node);
			if (temp != null) acts.push(temp);
		}
	}
	
	if (acts.length > 0) // trigger is attached to at least one action
	{
		popup_remove_trigger_open(trig_num, acts);
	}
	else // trigger is not attached to an action
	{
		remove_trigger(trig_num);
	}
}

function remove_trigger(trig_num)
{
	var trig;
	var num_actions;
	var acts;
	var start_node;
	var act;
	
	trig = triggers[trig_num];
	
	// DELETE TRIGGER
	
	triggers.splice(trig_num, 1);
	
	// DELETE ATTACHED ACTIONS
	
	num_actions = trig.fanout.length;
	
	acts = new Array();
	if (num_actions > 0) // trigger might be attached to at least one action
	{
		for (var i = 0; i < num_actions; i++)
		{
			start_node = trig.fanout[i];
			temp = get_action(start_node);
			if (temp != null) acts.push(temp);
		}
	}
	
	for (var i = 0; i < acts.length; i++)
	{
		act = acts[i];
	
		// remove from pathway
		act.fanin.fanout = null;
		
		// remove from list
		for (var j = 0; j < actions.length; j++)
		{
			if (actions_match(act, actions[j]))
			{
				actions.splice(j, 1);
			}
		}
	}
	
	update_prog_page();
	popup_all_purpose_close();
}

function remove_action(act_num)
{
	var action;
	
	action = actions[act_num];
	
	// remove from pathway
	action.fanin.fanout = null;
	
	// remove from list
	actions.splice(act_num, 1);
	
	update_prog_page();
	popup_all_purpose_close();
}

function popup_remove_trigger_open(trig_num, acts)
{
	var html_title, html_message, html_buttons;
	var d, o, a;
	
	number_actions();
	
	html_title = "Remove Trigger";
	
	html_message = "Trigger " + (trig_num+1) + " is attached to the following actions:<br>";
	for (var i = 0; i < acts.length; i++)
	{
		html_message = html_message + "<b>" + (acts[i].number+1) + "</b><br>";
	}
	html_message = html_message + "Deleting this trigger will also delete these actions.<br>Continue?<br><br>";

	html_buttons = "<button type='button' onclick='remove_trigger(" + trig_num + ")'>Remove Trigger</button>" +
	               "<button type='button' onclick='popup_all_purpose_close()'>Cancel</button>";
	
	popup_all_purpose_open([html_title, html_message, html_buttons], false);
}

function popup_remove_action_open(act_num)
{
	var html_title, html_message, html_buttons;
	
	html_title = "Remove Action";
	html_message = "Are you sure you want to remove Action " + (act_num+1) + "?<br><br>";
	html_buttons = "<button type='button' onclick='remove_action(" + act_num + ")'>Remove Action</button>" +
	               "<button type='button' onclick='popup_all_purpose_close()'>Cancel</button>";
	
	popup_all_purpose_open([html_title, html_message, html_buttons], false);
}

/*** PROCESS: ADD TRIGGER OR ACTION ***/

function display_devices(str_devices)
{
	var html = ""; // main body of HTML code to be inserted into page
	var devices = str_devices.split("|");
	var device_name;
	
	if (devices[0] == "no_devices") // empty list
		html = "-- No Devices --";
		
	else // list has items
	{
		html = "<table border='1' style='width:100%;border:3px solid black;border-collapse:collapse;'>"; // establish table
		for (var i = 0; i < devices.length; i++) // cycles through all devices
		{
			device_name = devices[i];
			html = html + "<tr><td><div class='col_1'>" + device_name + "</div><div class='col_2'>" +
			              "<button type='button' class='button_list' onclick='http_post_request(\"device_info|" + device_name + "\", 1, \"addEditProgram\")'>Add</button>" +
			              "</div></td></tr>";
		}
		html = html + "</table>";
	}
	
	// insert HTML code
	document.getElementById("device_list").innerHTML = html;
}

function update_dev(xml_string, device_name)
{
	var parser;
	
	parser = new DOMParser();

	dev.name = device_name;
	dev.desc = parser.parseFromString(xml_string, "text/xml");
	
	popup_add_trig_act();
}

function popup_add_trig_act()
{
	var num_triggers, num_actions;
	var html_title, html_message, html_buttons;
	
	num_triggers = dev.desc.getElementsByTagName("triggers")[0].childNodes.length;
	num_actions = dev.desc.getElementsByTagName("actions")[0].childNodes.length;
	
	html_title = "Add " + dev.name;
	html_message = "";
	html_buttons = "";
	if (num_triggers > 0)
	{
		html_buttons = html_buttons + "<button type='button' onclick='popup_add_trig_1()'>Add as Trigger</button>";
	}
	if (num_actions > 0 && triggers.length > 0) // can't add actions unless there are triggers
	{
		html_buttons = html_buttons + "<button type='button' onclick='popup_add_act_1()'>Add as Action</button>";
	}
	
	popup_all_purpose_open([html_title, html_message, html_buttons], true);
}

function popup_add_trig_1()
{
	var num_triggers;
	var html_title, html_message, html_buttons;
	var trig_name, trig_arg;
	var func;
	
	num_triggers = dev.desc.getElementsByTagName("triggers")[0].childNodes.length;
	
	html_title = "Add " + dev.name;
	html_message = "What type of trigger?";
	html_buttons = "";
	for (var i = 0; i < num_triggers; i++)
	{
		trig_name = dev.desc.getElementsByTagName("triggers")[0].childNodes[i].childNodes[0].childNodes[0].nodeValue;
		trig_arg = dev.desc.getElementsByTagName("triggers")[0].childNodes[i].childNodes[1];
		if (trig_arg != undefined) // trigger has an argument
			func = "popup_add_trig_2(\"" + i + "\")";
		else // trigger does not have an argument
			func = "add_trig(\"" + i + "\", \"\")";
		html_buttons = html_buttons + "<button type='button' onclick='" + func + "' style='width:75%;'>" + trig_name + "</button><br>";
	}
	html_buttons = html_buttons + "<br><button type='button' onclick='popup_add_trig_act()'>Back</button>";
	
	popup_all_purpose_open([html_title, html_message, html_buttons], true);
}

function popup_add_trig_2(trig_num)
{
	var trig_name;
	var arg;
	var arg_name, arg_type, arg_id;
	var html_title, html_message, html_buttons;

	trig_name = dev.desc.getElementsByTagName("triggers")[0].childNodes[trig_num].childNodes[0].childNodes[0].nodeValue;
	arg = dev.desc.getElementsByTagName("triggers")[0].childNodes[trig_num].childNodes[1];
	arg_name = arg.childNodes[0].nodeValue;
	arg_type = arg.getAttribute("type");
	arg_id =   arg.getAttribute("id");
	
	html_title = "Add " + dev.name;
	html_message = "Please enter an argument.<br><b>\"" + trig_name + "\"</b><br>";
	html_buttons = "";
	switch (arg_type)
	{
		case "Integer":
			html_buttons = html_buttons +
			"<input type='text' id='input'> (" + arg_id + ")<br>" +
			"<button type='button' onclick='check_arg(\"Integer\", \"input\", 1, \"" + trig_num + "\")'>Submit</button>";
			break;
		case "Floating-Point":
			html_buttons = html_buttons +
			"<input type='text' id='input'> (" + arg_id + ")<br>" +
			"<button type='button' onclick='check_arg(\"Floating-Point\", \"input\", 1, \"" + trig_num + "\")'>Submit</button>";
			break;
		case "Boolean":
			html_buttons = html_buttons +
			"<button type='button' onclick='check_arg(\"Boolean\", \"1\", 1, \"" + trig_num + "\")'>Turn On</button>" +
			"<button type='button' onclick='check_arg(\"Boolean\", \"0\", 1, \"" + trig_num + "\")'>Turn Off</button>";
			break;
		/*
		case "Date":
		
			break;
		case "Enumerator":
		
			break;
		*/
		default: // "String"
			html_buttons = html_buttons +
			"<input type='text' id='input'><br>" +
			"<button type='button' onclick='check_arg(\"String\", \"input\", 1, \"" + trig_num + "\")'>Submit</button>";
			break;
	}
	html_buttons = html_buttons + "<br><button type='button' onclick='popup_add_trig_1()'>Back</button>";
	
	popup_all_purpose_open([html_title, html_message, html_buttons], true);
}

function add_trig(trig_num, arg)
{
	var trigger, check;
	var d, c, a;
	
	d = dev.name;
	c = dev.desc.getElementsByTagName("triggers")[0].childNodes[trig_num].childNodes[0].childNodes[0].nodeValue;
	if (arg != "")
	a = arg;
	
	// create new trigger
	trigger =      new Object(); // boom
	trigger.type = "t";          // (aids searches)
	
	// assign properties to new trigger
	trigger.device =    d;
	trigger.condition = c;
	if (a != undefined)
	{
		trigger.args = new Array();
		trigger.args.push(a);
	}
	
	// check to see if trigger already exists
	check = get_trigger(trigger); // if found, returns trigger; if not found, returns null
	if (check != null) // trigger was found
	{
		alert("Trigger already exists.");
	}
	else // trigger was not found
	{
		trigger.fanout = new Array(); // create new array of pathways
		triggers.push(trigger); // add trigger to list
		update_prog_page();
		popup_all_purpose_close();
	}
}

function popup_add_act_1()
{
	var num_actions;
	var html_title, html_message, html_buttons;
	var act_name, act_arg;
	var func;
	
	num_actions = dev.desc.getElementsByTagName("actions")[0].childNodes.length;
	
	html_title = "Add " + dev.name;
	html_message = "What type of action?";
	html_buttons = "";
	for (var i = 0; i < num_actions; i++)
	{
		act_name = dev.desc.getElementsByTagName("actions")[0].childNodes[i].childNodes[0].childNodes[0].nodeValue;
		act_arg = dev.desc.getElementsByTagName("actions")[0].childNodes[i].childNodes[1].childNodes[0];
		if (act_arg != undefined) // action has an argument
			func = "popup_add_act_2(\"" + i + "\")";
		else // action does not have an argument
			func = "window_extend(\"triggers\"); popup_add_act_3(\"" + i + "\", \"\")";
		html_buttons = html_buttons + "<button type='button' onclick='" + func + "' style='width:75%;'>" + act_name + "</button><br>";
	}
	html_buttons = html_buttons + "<br><button type='button' onclick='popup_add_trig_act()'>Back</button>";
	
	popup_all_purpose_open([html_title, html_message, html_buttons], true);
}

function popup_add_act_2(act_num)
{
	var act_name;
	var arg;
	var arg_name, arg_type, arg_id;
	var html_title, html_message, html_buttons;

	act_name = dev.desc.getElementsByTagName("actions")[0].childNodes[act_num].childNodes[0].childNodes[0].nodeValue;
	arg = dev.desc.getElementsByTagName("actions")[0].childNodes[act_num].childNodes[1].childNodes[0];
	arg_name = arg.childNodes[0].nodeValue;
	arg_type = arg.getAttribute("type");
	arg_id =   arg.getAttribute("id");
	
	html_title = "Add " + dev.name;
	html_message = "Please enter an argument.<br><b>\"" + act_name + "\"</b><br>";
	html_buttons = "";
	switch (arg_type)
	{
		case "Integer":
			html_buttons = html_buttons +
			"<input type='text' id='input'> (" + arg_id + ")<br>" +
			"<button type='button' onclick='check_arg(\"Integer\", \"input\", 0, \"" + act_num + "\")'>Submit</button>";
			break;
		case "Floating-Point":
			html_buttons = html_buttons +
			"<input type='text' id='input'> (" + arg_id + ")<br>" +
			"<button type='button' onclick='check_arg(\"Floating-Point\", \"input\", 0, \"" + act_num + "\")'>Submit</button>";
			break;
		case "Boolean":
			html_buttons = html_buttons +
			"<button type='button' onclick='check_arg(\"Boolean\", \"1\", 0, \"" + act_num + "\")'>Turn On</button>" +
			"<button type='button' onclick='check_arg(\"Boolean\", \"0\", 0, \"" + act_num + "\")'>Turn Off</button>";
			break;
		/*
		case "Date":
		
			break;
		case "Enumerator":
		
			break;
		*/
		default: // "String"
			html_buttons = html_buttons +
			"<input type='text' id='input'><br>" +
			"<button type='button' onclick='check_arg(\"String\", \"input\", 0, \"" + act_num + "\")'>Submit</button>";
			break;
	}
	html_buttons = html_buttons + "<br><button type='button' onclick='popup_add_act_1()'>Back</button>";
	
	popup_all_purpose_open([html_title, html_message, html_buttons], true);
}

function popup_add_act_3(act_num, arg)
{
	var html_title, html_message, html_buttons;
	
	number_triggers();
	
	html_title = "Add " + dev.name;
	html_message = "What combination of triggers should result in this action?<br><br>" +
	               "Represent triggers with numbers, groups with brackets, AND operations with '&', and OR operations with '+'.<br><br>" +
				   "Example: ( <b>1</b> & <b>2</b> ) + <b>3</b><br><br>";
	html_buttons = "<input type='text' id='input' style='width:75%;'><br>" +
	               "<button type='button' onclick='add_act(\"" + act_num + "\", \"" + arg + "\", \"input\")'>Submit</button><br>" +
	               "<button type='button' onclick='window_normalize(); popup_add_act_1()'>Back</button>";
	
	popup_all_purpose_open([html_title, html_message, html_buttons], true);
}

/*
function popup_add_act_3(act_num, arg)
{
	var html_title, html_message, html_buttons;
	
	number_triggers();
	
	html_title = "Add " + dev.name;
	html_message = "Which trigger should result in this action?";
	html_buttons = "";
	for (var i = 0; i < triggers.length; i++)
	{
		t_num = triggers[i].number;
		html_buttons = html_buttons + "<button type='button' onclick='add_act(\"" + act_num + "\", \"" + arg + "\", \"" + i + "\")' style='width:75%;'>" + (t_num+1) + "</button><br>";
	}
	html_buttons = html_buttons + "<br><button type='button' onclick='popup_add_act_1()'>Back</button>";
	
	popup_all_purpose_open([html_title, html_message, html_buttons], true);
}

function add_act(act_num, arg, trig_num)
{
	var action;
	var d, o, a;
	var node;

	d = dev.name;
	o = dev.desc.getElementsByTagName("actions")[0].childNodes[act_num].childNodes[0].childNodes[0].nodeValue;
	if (arg != "")
	a = arg;
	
	// create new action
	action =      new Object(); // boom
	action.type = "a";          // (aids searches)
	
	// assign properties to new action
	action.device =    d;
	action.operation = o;
	if (a != undefined)
	{
		action.args = new Array();
		action.args.push(a);
	}
	
	// attach trigger
	action.fanin = triggers[trig_num];
	triggers[trig_num].fanout.push(action);
	
	actions.push(action);
	update_prog_page();
	popup_all_purpose_close();
}
*/

function add_act(act_num, arg, input)
{
	var str;
	var action;
	var d, o, a;
	
	str = document.getElementById(input).value;
	
	// remove whitespace
	str = str.replace(/\s/g, "");
	
	// check for invalid characters
	if (invalid_chars(str))
	{
		alert("Invalid characters.");
		return;
	}
	
	// check for improper combination
	if (improper_combo(str))
	{
		alert("Improper combination.");
		return;
	}
	
	action = new Object();
	
	action.fanin = tree(str, action);
	if (action.fanin == null) return; // error
	
	// if execution reaches this point, action was successfully attached to some combination of triggers
	
	d = dev.name;
	o = dev.desc.getElementsByTagName("actions")[0].childNodes[act_num].childNodes[0].childNodes[0].nodeValue;
	a = arg;
	
	action.type = "a";
	action.device = d;
	action.operation = o;
	if (a != "")
	{
		action.args = new Array();
		action.args.push(a);
	}
	
	actions.push(action);
	update_prog_page();
	popup_all_purpose_close();
}

function improper_combo(str)
{
	var debugging;
	var num_op, num_cl;
	var mode;

	debugging = false;
	
	// CHECK FOR CORRECT NUMBER OF BRACKETS
	
	num_op = indeces_of(str, "(").length;
	num_cl = indeces_of(str, ")").length;
	if (num_op != num_cl)
	{
		if (debugging) alert("Brackets");
		return true;
	}
	
	// CHECK FOR EVERYTHING ELSE (step through string)
	
	num_op = 0; // maintained as a current value
	num_cl = 0; // maintained as a current value
	mode = "n"; // can be "&", "+", or "n" (none)
	for (var i = 0; i < str.length; i++)
	{
		x = str[i];
		y = str[i+1];
	
		// first character
		if (i == 0)
		{
			if (x != "(" && (x < "0" || x > "9")) // not an opening bracket and not a number
			{
				if (debugging) alert("First character.");
					return true;
			}
		}
		
		// all characters except last character
		if (i < str.length-1)
		{
			switch (x)
			{
				case "(":
					if (y != "(" && (y < "0" || y > "9")) // next character is not an opening bracket and not a number
					{
						if (debugging) alert("(");
						return true;
					}
					mode = "n";
					num_op++;
					break;
				case ")":
					if (y != "+" && y != "&") // next character is not a "&" and not a "+"
					{
						if (debugging) alert(")");
						return true;
					}
					if (num_op <= num_cl) // closing bracket appears before opening bracket
					{
						if (debugging) alert("Order.");
						return true;
					}
					mode = "n";
					num_cl++;
					break;
				case "&":
					if (y != "(" && (y < "0" || y > "9")) // next character is not an opening bracket and not a number
					{
						if (debugging) alert("&");
						return true;
					}
					if (mode == "+") // wrong mode
					{
						if (debugging) alert("&, mode");
						return true;
					}
					if (mode == "n")
						mode = "&";
					break;
				case "+":
					if (y != "(" && (y < "0" || y > "9")) // next character is not an opening bracket and not a number
					{
						if (debugging) alert("+");
						return true;
					}
					if (mode == "&") // wrong mode
					{
						if (debugging) alert("+, mode");
						return true;
					}
					if (mode == "n")
						mode = "+";
					break;
				default: // number
					if (y == "(") // next character is an opening bracket
					{
						if (debugging) alert("Number.");
						return true;
					}
					break;
			}
		}
		
		// last character
		if (i == str.length-1)
		{
			if (x != ")" && (x < "0" || x > "9")) // not a closing bracket and not a number
			{
				if (debugging) alert("Last character.");
				return true;
			}
		}
	}
	
	return false;
}

function invalid_chars(str)
{
	var c;

	for (var i = 0; i < str.length; i++)
	{
		c = str[i];
		if (c != "(" && c != ")" && c != "&" && c != "+" && (c < "0" || c > "9")) return true;
	}
	
	return false;
}

function tree(trigs, fanout)
{
	var trigs_new;
	var trigger, node;
	var pos_ands, pos_ors;
	var pos_ands_ext, pos_ors_ext;
	var temp_str, temp_ptr, num_op_bracks, num_cl_bracks;
	var start, end;

	// END CASE
	
	if (trigs.indexOf("&") == -1 && trigs.indexOf("+") == -1)
	{
		// check: is trigs an integer?
		for (var i = 0; i < trigs.length; i++)
		{
			if (trigs[i] < "0" || trigs[i] > "9")
			{
				alert("Not an integer.");
				return null; // (error)
			}
		}
		
		// check: does trigs point to a trigger?
		if (triggers[trigs-1] == undefined)
		{
			alert("Trigger does not exist.");
			return null; // (error)
		}
	
		trigger = triggers[trigs-1];
		trigger.fanout.push(fanout);
		return trigger;
	}
	
	// RECURSIVE CASE
	
	// find positions of all & and + symbols
	pos_ands = indeces_of(trigs, "&");
	pos_ors = indeces_of(trigs, "+");
	
	trigs_new = new Array();
	
	if (pos_ands.length > 0) // & symbols were found (NOTE: It is still possible that no EXTERNAL & symbols are present)
	{
		pos_ands_ext = new Array(); // positions of all & symbols that are NOT inside any brackets
	
		for (var i = 0; i < pos_ands.length; i++) // cycle through all & symbols
		{
			// grab everything in front of the current & symbol
			temp_str = trigs.substring(0, pos_ands[i]);
			
			// determine the number of opening and closing brackets in temp_str
			num_op_bracks = indeces_of(temp_str, "(").length;
			num_cl_bracks = indeces_of(temp_str, ")").length;
			
			// if the number of opening brackets matches the number of closing brackets,
			// the current & symbol is NOT inside any brackets and can therefore be used
			// to split trigs
			if (num_op_bracks == num_cl_bracks) pos_ands_ext.push(pos_ands[i]);
		}
		
		if (pos_ands_ext.length > 0) // external & symbols are indeed present
		{
			// first piece
			start = 0;               // included
			end =   pos_ands_ext[0]; // up to but not included
			trigs_new.push(trigs.substring(start, end));
			
			for (var j = 0; j < pos_ands_ext.length-1; j++)
			{
					// middle piece(s)
					start = pos_ands_ext[j] + 1; // included
					end =   pos_ands_ext[j+1]        // up to but not included
					trigs_new.push(trigs.substring(start, end));
			}
			
			// last piece
			start = pos_ands_ext[pos_ands_ext.length-1] + 1; // included
			end =   trigs.length;                            // up to but not included
			trigs_new.push(trigs.substring(start, end));
			
			node = new Object();
			node.type = "&";
			node.fanin = new Array();
			node.fanout = fanout;
			
			for (var k = 0; k < trigs_new.length; k++)
			{
				temp_str = trigs_new[k];
				temp_ptr = tree(temp_str, node);
				if (temp_ptr == null) return null; // error
				else node.fanin.push(temp_ptr); // recursive call; acquire children
			}
			
			return node;
		}
	}
	else // no & symbols were found (NOTE: It is still possible that some + symbols are NOT external)
	{
		pos_ors_ext = new Array(); // positions of all + symbols that are NOT inside any brackets
		
		for (i = 0; i < pos_ors.length; i++) // cycle through all + symbols
		{
			// grab everything in front of the current + symbol
			temp_str = trigs.substring(0, pos_ors[i]);
			
			// determine the number of opening and closing brackets in temp_str
			num_op_bracks = indeces_of(temp_str, "(").length;
			num_cl_bracks = indeces_of(temp_str, ")").length;
			
			// if the number of opening brackets matches the number of closing brackets,
			// the current & symbol is NOT inside any brackets and can therefore be used
			// to split trigs
			if (num_op_bracks == num_cl_bracks) pos_ors_ext.push(pos_ors[i]);
		}
		
		// since no & symbols were found, therefore external + symbols must be present
		
		// first piece
		start = 0;              // included
		end =   pos_ors_ext[0]; // up to but not included
		trigs_new.push(trigs.substring(start, end));
		
		for (j = 0; j < pos_ors_ext.length-1; j++)
		{
				// middle piece(s)
				start = pos_ors_ext[j] + 1; // included
				end =   pos_ors_ext[j+1]        // up to but not included
				trigs_new.push(trigs.substring(start, end));
		}
			
		// last piece
		start = pos_ors_ext[pos_ors_ext.length-1] + 1; // included
		end =   trigs.length;                          // up to but not included
		trigs_new.push(trigs.substring(start, end));
		
		node = new Object();
		node.type = "+";
		node.fanin = new Array();
		node.fanout = fanout;
		
		for (k = 0; k < trigs_new.length; k++)
		{
			temp_str = trigs_new[k];
			temp_ptr = tree(temp_str, node);
			if (temp_ptr == null) return null; // error
			else node.fanin.push(temp_ptr); // recursive call; acquire children
		}
		
		return node;
	}
}

function check_arg(type, input, trig_actn, num) // if trig_actn = 1 proceed with trigger creation, otherwise proceed with action creation
{
	var no_error = true;
	var value;
	
	switch (type)
	{
		case "Integer":
			value = document.getElementById(input).value; // grab
			for (i = 0; i < value.length; i++) if (value[i] < '0' || value[i] > '9') no_error = false; // check
			break;
		case "Floating-Point":
			value = document.getElementById(input).value; // grab
			if (value.isNaN()) no_error = false; // check
			break;
		case "Boolean":
			if (input == 1) value = "true";
			else            value = "false";
			// no check
			break;
		default: // "String"
			value = document.getElementById(input).value; // grab
			// no check
			break;
	}
	
	if (no_error)
	{
		if (trig_actn)
		{
			add_trig(num, value);
		}
		else
		{
			window_extend("triggers");
			popup_add_act_3(num, value);
		}
	}
	else // error
	{
		alert("Input error.");
	}
}

/*** PROCESS: SAVE PROGRAM ***/

function save_program_attempt_1()
{
	var start_node, temp;
	var attached;

	// check for triggers or actions
	if (triggers.length < 1 || actions.length < 1)
	{
		save_program_error(0)
		return;
	}
	
	// check for triggers that are not attached to actions
	for (var i = 0; i < triggers.length; i++)
	{
		trig = triggers[i];
		attached = false;
		if (trig.fanout.length > 0) // trigger has parent; trigger might be attached to at least one action
		{
			for (var j = 0; j < trig.fanout.length; j++)
			{
				start_node = trig.fanout[j];
				temp = get_action(start_node);
				if (temp != null) // trigger is attached to an action
				{
					attached = true;
				}
			}
		}
		if (!attached)
		{
			save_program_error(1);
			return;
		}
	}
	
	// if execution reaches this point, there is at least one trigger attached to at least one action
	
	save_program_attempt_2();
}

function save_program_error(code)
{
	var html_title, html_message, html_buttons;
	
	switch (code)
	{
		case 0:
			html_title = "Couldn't Save";
			html_message = "A program requires at least one trigger and at least one action.<br><br>";
			html_buttons = "<button type='button' onclick='popup_all_purpose_close()'>Close</button>";
			break;
		case 1:
			html_title = "Warning!";
			html_message = "At least one trigger is not attached to an action.<br>" +
			               "It and any others like it will be <b>deleted</b> if the program is saved.<br>Continue anyway?<br><br>";
			html_buttons = "<button type='button' onclick='save_program_attempt_2()'>Save Program</button>" +
			               "<button type='button' onclick='popup_all_purpose_close()'>Cancel</button>";
			break;
		default:
			alert("Error! (To the developer: an error code from function save_program_attempt_1() is not accomodated in save_program_error())");
			break;
	}
	
	popup_all_purpose_open([html_title, html_message, html_buttons], false);
}

function save_program_attempt_2()
{
	var html_title, html_message, html_buttons;

	if (document.title == "New Program")
	{
		html_title = "Program Name";
		html_message = "Please name your new program.";
		html_buttons = "<input type='text' id='program_name'><br><br>" +
		               "<button type='button' onclick='check_prog_name(\"program_name\")'>Submit</button>" +
	                   "<button type='button' onclick='popup_all_purpose_close()'>Cancel</button>";
		popup_all_purpose_open([html_title, html_message, html_buttons], false);
	}
	else
	{
		save_program("");
	}
}

function check_prog_name(input)
{
	var prog_name;
	var c;
	var html_message;

	prog_name = document.getElementById(input).value;
	for (var i = 0; i < prog_name.length; i++)
	{
		c = prog_name[i];
		if ( (c < "A" || c > "Z") &&                         // "not an uppercase letter"
		     (c < "a" || c > "z") &&                         // "not a lowercase letter"
			 (c < "0" || c > "9") &&                         // "not a number"
			 c != "." && c != "-" && c != "~" && c != "_" && // "not an allowed URL character"
			 c != " " && c != "'" )                          // "not an allowed application character"
		{
			html_message = document.getElementById("popup_message").innerHTML;
			html_message = "Please name your new program." +
					       "<br>Please use only letters, numbers, spaces, and the following characters:<br>. - ~ _ '";
			document.getElementById("popup_message").innerHTML = html_message;
			return;
		}
	}
	
	// if execution reaches this point, the user's desired program name is a-okay
	
	save_program(input);
}

function save_program(input)
{
	var action;
	var prog_rep, act_rep;
	var op, title, request;
	
	prog_rep = "";
	for (var i = 0; i < actions.length; i++)
	{
		action = actions[i];
		act_rep = "";
		
		if (i > 0) act_rep = act_rep + "::,";
		
		// create action representation
		act_rep = act_rep + trig_rep(action.fanin) + "::->" + action.device + ":::" + action.operation;
		if (action.args != undefined)
		{
			for (var j = 0; j < action.args.length; j++)
			{
				act_rep = act_rep + ":::" + action.args[j];
			}
		}
		
		// add action representation to program representation
		prog_rep = prog_rep + act_rep;
	}
	
	if (input == "") // existing program (user is editing)
	{
		op = "edit_service";
		title = document.title;
	}
	else // new program (user is creating)
	{
		op = "create_service";
		title = document.getElementById(input).value;
	}
		
	request = op + "|" + title + "|" + prog_rep;
	http_post_request(request, 1, "");
}

function trig_rep(node) // recursive function
{
	var rep;
	
	// END CASE: node is a trigger
	
	if (node.type == "t")
	{
		rep = "::(" + node.device + ":::" + node.condition;
		if (node.args != undefined)
		{
			for (var i = 0; i < node.args.length; i++)
			{
				rep = rep + ":::" + node.args[i];
			}
		}
		rep = rep + "::)";
	}
	
	// RECURSIVE CASE 1: node is a "&" node
	
	if (node.type == "&")
	{
		rep = "::(";
		for (var i = 0; i < node.fanin.length; i++)
		{
			rep = rep + trig_rep(node.fanin[i])
			if (i < node.fanin.length-1) // child is not last child
			{
				rep = rep + "&";
			}
		}
		rep = rep + "::)";
	}
	
	// RECURSIVE CASE 2: node is a "+" node
	
	if (node.type == "+")
	{
		rep = "::(";
		for (var i = 0; i < node.fanin.length; i++)
		{
			rep = rep + trig_rep(node.fanin[i])
			if (i < node.fanin.length-1) // child is not last child
			{
				rep = rep + "+";
			}
		}
		rep = rep + "::)";
	}
	
	return rep;
}

function confirm_create_service(response, title)
{
	if (response != "service_created")
	{
		alert(response);
		return;
	}

	alert("Program created!");
	get_page("addEditProgram", title);
}

function confirm_edit_service(response, title)
{
	if (response != "service_edited")
	{
		alert(response);
		return;
	}
	
	alert("Program edited!");
	get_page("addEditProgram", title);
}

/*** UTILITY FUNCTIONS ***/

function popup_all_purpose_open(htmls, c_button) // htmls: [html_title, html_message, html_buttons]
{
	document.getElementById("popup_title").innerHTML = htmls[0];
	document.getElementById("popup_message").innerHTML = htmls[1];
	document.getElementById("popup_buttons").innerHTML = htmls[2];
	
	// show / hide cancel button
	if (c_button)
		document.getElementById("button_popup_ap_back").style.display = "block";
	else
		document.getElementById("button_popup_ap_back").style.display = "none";
	
	// show popup and darken background
	document.getElementById("popup_all_purpose").style.display = "block";
	document.getElementById("dark_bkgnd").style.display = "block";
}

function popup_all_purpose_close()
{
	// hide cancel button
	document.getElementById("button_popup_ap_back").style.display = "none";
	
	window_normalize();

	// hide popup and brighten background
	document.getElementById("popup_all_purpose").style.display = "none";
	document.getElementById("dark_bkgnd").style.display = "none";
}

function window_extend(div) // div = "triggers" or "actions"
{
	// attach popup to top of window
	document.getElementById("popup_all_purpose").style.top = "0";
	document.getElementById("popup_all_purpose").style.marginTop = "0";
	
	// brighten currently active triggers / actions
	if (div == "triggers")
	{
		document.getElementById("triggers_list").style.zIndex = "2";
	}
	else
		document.getElementById("actions_list").style.zIndex = "2";
}

function window_normalize()
{
	// return popup to center of window
	document.getElementById("popup_all_purpose").style.top = "50%";
	document.getElementById("popup_all_purpose").style.marginTop = "-200px";
	
	// darken currently active triggers and actions
	document.getElementById("triggers_list").style.zIndex = "0";
	document.getElementById("actions_list").style.zIndex = "0";
}

function get_trigger(trigger) // this function returns the trigger, if found, or null, if not found
{
	for (var i = 0; i < triggers.length; i++)
	{
		if (triggers_match(trigger, triggers[i])) return triggers[i];
	}
	
	return null;
}

function get_action(curr) // this (recursive) function attempts to return the action atttached to a node
{
	// END CASE 1: previous node has no parent
	if (curr == null)
		return null;

	// END CASE 2: current node is an action
	if (curr.type == "a")
		return curr;
	
	// RECURSIVE CASE: current node is an AND or OR node
	else
		return get_action(curr.fanout);
}

function triggers_match(trig1, trig2)
{
	if (trig1.device != trig2.device)
		return false;
	if (trig1.condition != trig2.condition)
		return false;
	
	// if the two triggers have the same device and the same condition,
	// both must either have no arguments or the same number of arguments
	
	if (trig1.args != undefined && trig2.args != undefined)
	{
		for (var i = 0; i < trig1.args; i++)
		{
			if (trig1.args[i] != trig2.args[i]) return false;
		}
	}
	
	// if execution reaches this point, the two triggers have the same
	// device, condition, and arguments
	
	return true;
}

function actions_match(act1, act2)
{
	if (act1.device != act2.device)
		return false;
	if (act1.operation != act2.operation)
		return false;
	
	// if the two actions have the same device and the same operation,
	// both must either have no arguments or the same number of arguments
	
	if (act1.args != undefined && act2.args != undefined)
	{
		for (var i = 0; i < act1.args; i++)
		{
			if (act1.args[i] != act2.args[i]) return false;
		}
	}
	
	// if execution reaches this point, the two triggers have the same
	// device, condition, and arguments
	
	return true;
}

function number_triggers()
{
	for (var i = 0; i < triggers.length; i++)
	{
		triggers[i].number = i;
	}
}

function number_actions()
{
	for (var i = 0; i < actions.length; i++)
	{
		actions[i].number = i;
	}
}

/**************************************************************************************************
 *                                                                                                *
 *                    ACCOUNT PAGE                                                                *
 *                                                                                                *
 **************************************************************************************************/
 
function show(user_passn) // user_passn: true = username settings, false = password settings
{
	var div;

	// hide all settings
	document.getElementById("user_settings").style.display = "none";
	document.getElementById("pass_settings").style.display = "none";

	// show appropriate settings
	div = "user_settings";
	if (!user_passn) div = "pass_settings";
	document.getElementById(div).style.display = "block";
}

function show(to_show) // possible values for to_show: "nothing", "user_settings", "pass_settings"
{
	var div;

	// hide all settings
	document.getElementById("user_settings").style.display = "none";
	document.getElementById("pass_settings").style.display = "none";
	
	if (to_show == "nothing") return; // done
	
	// clear all fields and error message
	document.getElementById("user_old").value = "";
	document.getElementById("user_new").value = "";
	document.getElementById("pass_old").value = "";
	document.getElementById("pass_new").value = "";
	document.getElementById("edit_username_error").innerHTML = "";
	document.getElementById("edit_password_error").innerHTML = "";
	
	// show appropriate settings
	div = to_show;
	document.getElementById(div).style.display = "block";
}

function edit_username()
{
	var user_old, user_new;
	var request;

	user_old = document.getElementById("user_old").value;
	user_new = document.getElementById("user_new").value;
	request = "edit_user_name|" + user_old + "|" + user_new;
	http_post_request(request, true, "");
}

function confirm_edit_username(response)
{
	var error;

	switch (response)
	{
		case "name_edited":
			show("nothing");
			alert("Username successfully changed!");
			break;
		case "edit_failed":
			error = "Edit failed. Ensure that the old username is spelled correctly."
			document.getElementById("edit_username_error").innerHTML = error;
			break;
		default: // invalid_num_inputs
			alert(response);
			break;
	}
}

function edit_password()
{
	var pass_old, pass_new;
	var request;

	pass_old = document.getElementById("pass_old").value;
	pass_new = document.getElementById("pass_new").value;
	request = "edit_password|" + pass_old + "|" + pass_new;
	http_post_request(request, true, "");
}

function confirm_edit_password(response)
{
	var error;

	switch (response)
	{
		case "password_edited":
			show("nothing");
			alert("Password successfully changed!");
			break;
		case "edit_failed":
			error = "Edit failed. Ensure that the old password is spelled correctly."
			document.getElementById("edit_password_error").innerHTML = error;
			break;
		default: // invalid_num_inputs
			alert(response);
			break;
	}
}

var xml_timer =

'<?xml version="1.0" encoding="UTF-8"?>' +

'<peripheral class="CoolTimer" typeID="54">' +
	'<peripheral-type>My Timer</peripheral-type>' +
	
	
	'<triggers>' +
		'<trigger id="TIMER_EXPIRED">' +
			'<name>Timer Expired</name>' +
		'</trigger>' +
		
		'<trigger id="CURRENT_DATE">' +
			'<name>Current Date is</name>' +
			'<argument type="Date" id="TARGET_DATE">Target Date</argument>' +
		'</trigger>' +
		
		'<trigger id="TIMER_STOPPED">' +
			'<name>Timer was Stopped</name>' +
		'</trigger>' +
	'</triggers>' +
	
	'<actions>' +
		'<action id="STOP_TIMER">' +
			'<name>Stop Timer</name>' +
			'<function id="stopTimer">' +
			'</function>' +
		'</action>' +
		
		'<action id="SET_TIMER">' +
			'<name>Set Timer</name>' +
			'<function id="setTimer">' +
				'<argument type="Integer" id="seconds">Timer Seconds</argument>' +
			'</function>' +
		'</action>' +
	'</actions>' +
	
	'<statuses>' +
		'<status id="TIMER_VALUE">' +
			'<name>Timer Value</name>' +
		'</status>' +
	'</statuses>' +
	
'</peripheral>';




