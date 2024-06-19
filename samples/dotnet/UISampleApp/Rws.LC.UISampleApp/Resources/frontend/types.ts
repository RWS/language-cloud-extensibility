export type ProjectImportance = {
    projectId: string;
    pending: boolean;
    importance?: "high" | "medium" | "low";
    id?: string;
};
