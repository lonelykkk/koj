import { PROBLEM_LEVEL } from "@/common/constants";

function submissionMemoryFormat(a, b) {
    if (a === null || a === '' || a === undefined) return "--";
    if (a === 0) return "0 KB";
    var c = 1024, d = b || 1, e = ["KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"],
        f = Math.floor(Math.log(a) / Math.log(c));
    return parseFloat((a / Math.pow(c, f)).toFixed(d)) + " " + e[f]
}

function submissionTimeFormat(time) {
    if (time === undefined || time === null || time === '') return '--'
    return time + 'ms'
}

function submissionLengthFormat(length) {
    if (length === undefined || length === null || length === '') return '--'
    return length + 'B'
}

function getLevelColor(level) {
    if (level != undefined && level != null && PROBLEM_LEVEL[level]) {
        return ('color: black !important; background-color: ' + PROBLEM_LEVEL[level] + ' !important;')
    } else {
        return 'color: #fff !important;background-color: rgb(255, 153, 0)!important;'
    }
}

function getLevelName(level) {
    if (level != undefined && level != null && PROBLEM_LEVEL[level]) {
        return PROBLEM_LEVEL[level]['name']
    } else {
        return 'unknow [' + level + ']'
    }
}

function examplesToString(objList) {
    if (objList.length == 0) {
        return "";
    }
    let result = ""
    for (let obj of objList) {
        result += "<input>" + obj.input + "</input><output>" + obj.output + "</output>"
    }
    return result
}

function stringToExamples(value) {
    let reg = "<input>([\\s\\S]*?)</input><output>([\\s\\S]*?)</output>";
    let re = RegExp(reg, "g");
    let objList = []
    let tmp;
    while (tmp = re.exec(value)) {
        objList.push({ input: tmp[1], output: tmp[2] })
    }
    return objList
}

// 去掉值为空的项，返回object
function filterEmptyValue(object) {
    let query = {}
    Object.keys(object).forEach(key => {
        if (object[key] || object[key] === 0 || object[key] === false) {
            query[key] = object[key]
        }
    })
    return query
}

export default {
    submissionMemoryFormat,
    submissionTimeFormat,
    submissionLengthFormat,
    getLevelColor,
    getLevelName,
    examplesToString,
    stringToExamples,
    filterEmptyValue
}