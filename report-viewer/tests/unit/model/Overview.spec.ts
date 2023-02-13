import {Overview} from "@/model/Overview";
import {Metric} from "@/model/Metric";
import {Cluster} from "@/model/Cluster";
test('overview', () => {
    const submissionFolderPath:Array<string> = ["C:\\Users\\23651\\Desktop\\JPlag\\core\\src\\test\\resources\\de\\jplag\\samples\\PartialPlagiarism"]
    const baseCodeFolderPath: string = "";
    const language: string = "Javac based AST plugin";
    const fileExtensions: Array<string> = [".java",".JAVA"];
    const matchSensitivity: number = 9;
    const dateOfExecution: string = "14/12/22";
    const durationOfExecution: number = 40;
    const metrics: Array<Metric> = [{
        "metricName": "AVG",
        "distribution": [1, 0, 2, 0, 0, 0, 0, 3, 0, 4],
        "metricThreshold": 0,
        "comparisons": [
            {
                "firstSubmissionId": "A",
                "secondSubmissionId": "C",
                "similarity": 0.9966329966329966
            },
            {
                "firstSubmissionId": "D",
                "secondSubmissionId": "A",
                "similarity": 0.7787255393878575
            },
            {
                "firstSubmissionId": "D",
                "secondSubmissionId": "C",
                "similarity": 0.7787255393878575
            },
            {
                "firstSubmissionId": "B",
                "secondSubmissionId": "D",
                "similarity": 0.2827868852459016
            },
            {
                "firstSubmissionId": "B",
                "secondSubmissionId": "A",
                "similarity": 0.2457689477557027
            },
            {
                "firstSubmissionId": "B",
                "secondSubmissionId": "C",
                "similarity": 0.2457689477557027
            },
            {
                "firstSubmissionId": "E",
                "secondSubmissionId": "A",
                "similarity": 0
            },
            {
                "firstSubmissionId": "E",
                "secondSubmissionId": "D",
                "similarity": 0
            },
            {
                "firstSubmissionId": "E",
                "secondSubmissionId": "B",
                "similarity": 0
            },
            {
                "firstSubmissionId": "E",
                "secondSubmissionId": "C",
                "similarity": 0
            }
        ],
        "description": "Average of both program coverages. This is the default similarity which works in most cases: Matches with a high average similarity indicate that the programs work in a very similar way."
    },{
        "metricName": "MAX",
        "distribution": [5, 1, 0, 0, 0, 0, 0, 0, 0, 4],
        "metricThreshold": 0,
        "comparisons": [
            {
                "firstSubmissionId": "A",
                "secondSubmissionId": "C",
                "similarity": 0.9966329966329966
            },
            {
                "firstSubmissionId": "B",
                "secondSubmissionId": "A",
                "similarity": 0.9766081871345029
            },
            {
                "firstSubmissionId": "B",
                "secondSubmissionId": "C",
                "similarity": 0.9766081871345029
            },
            {
                "firstSubmissionId": "D",
                "secondSubmissionId": "A",
                "similarity": 0.9639751552795031
            },
            {
                "firstSubmissionId": "D",
                "secondSubmissionId": "C",
                "similarity": 0.9639751552795031
            },
            {
                "firstSubmissionId": "B",
                "secondSubmissionId": "D",
                "similarity": 0.8070175438596491
            },
            {
                "firstSubmissionId": "E",
                "secondSubmissionId": "A",
                "similarity": 0
            },
            {
                "firstSubmissionId": "E",
                "secondSubmissionId": "D",
                "similarity": 0
            },
            {
                "firstSubmissionId": "E",
                "secondSubmissionId": "B",
                "similarity": 0
            },
            {
                "firstSubmissionId": "E",
                "secondSubmissionId": "C",
                "similarity": 0
            }
        ],
        "description": "Maximum of both program coverages. This ranking is especially useful if the programs are very different in size. This can happen when dead code was inserted to disguise the origin of the plagiarized program."
    }];
    const clusters: Array<Cluster> = [];
    const submissionIdsToComparisonFileName: Map<string,Map<string,string>> = new Map<string, Map<string, string>>();
    const overview: Overview = new Overview(submissionFolderPath,baseCodeFolderPath,language,fileExtensions,matchSensitivity,
        dateOfExecution,durationOfExecution,metrics,clusters,submissionIdsToComparisonFileName);
    expect(overview.language).toMatch("Javac based AST plugin");
    expect(overview.matchSensitivity).toBe(9);
    expect(overview.dateOfExecution).toMatch("14/12/22");
    expect(overview.durationOfExecution).toBe(40);
    expect(overview.metrics.length).toBe(2);
    expect(overview.metrics[0].metricName).toMatch("AVG");
    expect(overview.metrics[1].metricName).toMatch("MAX");
})