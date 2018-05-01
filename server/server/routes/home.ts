import { Application, Request, Response } from "express";

function register(app: Application): Application {
  app.get('/', (req: Request, res: Response) => {
    res.status(200);
    res.send('');
  });

  return app;
}

export default register;